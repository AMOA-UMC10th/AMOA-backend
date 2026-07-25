package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.entity.QCard;
import com.amoa.server.domain.card.entity.mapping.QCardDesignTag;
import com.amoa.server.domain.card.exception.CardException;
import com.amoa.server.domain.card.exception.code.CardErrorCode;
import com.amoa.server.domain.card.util.CardCursor;
import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.SortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QCard qCard = QCard.card;
    private static final QCardDesignTag qCardDesignTag = QCardDesignTag.cardDesignTag;

    // 검색 조건에 맞는 카드가 총 몇 개인지 조회
    @Override
    public Long countCards(CardSearchRequest request) {

        return queryFactory
                .select(qCard.count())
                .from(qCard)
                .where(qCard.deletedAt.isNull(),                    // 삭제되지 않은 카드만 조회
                        artTypeCondition(request.artType()),
                        priceCondition(request),
                        regionCondition(request.regionIds()),
                        designTagCondition(request.designTagIds())
                )
                .fetchOne();
    }

    // 카드 목록 조회
    @Override
    public List<Card> findCards(CardSearchRequest request, int size) {

        return queryFactory
                .selectFrom(qCard)
                .join(qCard.shop).fetchJoin()
                .join(qCard.shop.region).fetchJoin()
                .where(
                        qCard.deletedAt.isNull(),
                        cursorCondition(request.cursor(), request.sort()),                   // 커서 페이지네이션
                        artTypeCondition(request.artType()),
                        priceCondition(request),
                        regionCondition(request.regionIds()),
                        designTagCondition(request.designTagIds())
                )
                .orderBy(getOrder(request.sort()))                           // 정렬
                .limit(size + 1)
                .fetch();
    }

    // 더 넓은 조건(stage2)에 맞으면서, 더 좁은 조건(stage1)에는 맞지 않는 카드 수 조회
    // ID 집합이 아니라 stage1의 필터 조건 자체를 배제 조건으로 사용하므로
    // 페이지네이션 상태와 무관하게 항상 정확한 카운트를 반환한다.
    @Override
    public Long countCardsExcludingConditions(CardSearchRequest broaderRequest, CardSearchRequest narrowerRequest) {

        // BooleanExpression.and(null)은 안전하게 무시되므로 null 조건들도 그대로 체이닝 가능
        BooleanExpression narrowerMatch = qCard.deletedAt.isNull()
                .and(artTypeCondition(narrowerRequest.artType()))
                .and(priceCondition(narrowerRequest))
                .and(regionCondition(narrowerRequest.regionIds()))
                .and(designTagCondition(narrowerRequest.designTagIds()));

        return queryFactory
                .select(qCard.count())
                .from(qCard)
                .where(
                        qCard.deletedAt.isNull(),
                        artTypeCondition(broaderRequest.artType()),
                        priceCondition(broaderRequest),
                        regionCondition(broaderRequest.regionIds()),
                        designTagCondition(broaderRequest.designTagIds()),
                        narrowerMatch.not()
                )
                .fetchOne();
    }


    // 커서 페이지네이션(정렬 기준 값과 id를 함께 비교하는 복합 커서 방식)
    private BooleanExpression cursorCondition(
            String cursor,
            SortType sort
    ) {

        // 첫 페이지 조회 시 커서 조건 적용하지 않음
        if (cursor == null) {
            return null;
        }

        // 커서 분리
        CardCursor cardCursor = CardCursor.from(cursor);

        // 정렬 종류 확인(정렬 값이 없으면 기본 추천순)
        SortType sortType = sort == null
                ? SortType.RECOMMENDED
                : sort;

        return switch (sortType) {

            // 가격 낮은 순
            // 최소 가격이 높은 카드 조회
            // 최소 가격이 같으면 최대 가격이 더 높은 카드 조회
            // 가격 범위가 같으면 id가 높은 카드 조회
            case PRICE_ASC -> {

                Integer minPrice;
                Integer maxPrice;

                try {
                    if (cardCursor.secondValue() == null) {
                        throw new NumberFormatException();
                    }

                    minPrice = Integer.parseInt(cardCursor.value());
                    maxPrice = Integer.parseInt(cardCursor.secondValue());

                } catch (NumberFormatException | NullPointerException e) {
                    throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
                }

                yield qCard.minPrice.gt(minPrice)
                        .or(
                                qCard.minPrice.eq(minPrice)
                                        .and(qCard.maxPrice.gt(maxPrice))
                        )
                        .or(
                                qCard.minPrice.eq(minPrice)
                                        .and(qCard.maxPrice.eq(maxPrice))
                                        .and(qCard.id.gt(cardCursor.cardId()))
                        );
            }

            // 가격 높은 순
            // 최소 가격이 낮은 카드 조회
            // 최소 가격이 같으면 최대 가격이 더 낮은 카드 조회
            // 가격 범위가 같으면 id가 높은 카드 조회
            case PRICE_DESC -> {

                Integer minPrice;
                Integer maxPrice;

                try {
                    if (cardCursor.secondValue() == null) {
                        throw new NumberFormatException();
                    }

                    minPrice = Integer.parseInt(cardCursor.value());
                    maxPrice = Integer.parseInt(cardCursor.secondValue());

                } catch (NumberFormatException | NullPointerException e) {
                    throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
                }

                yield qCard.minPrice.lt(minPrice)
                        .or(
                                qCard.minPrice.eq(minPrice)
                                        .and(qCard.maxPrice.lt(maxPrice))
                        )
                        .or(
                                qCard.minPrice.eq(minPrice)
                                        .and(qCard.maxPrice.eq(maxPrice))
                                        .and(qCard.id.gt(cardCursor.cardId()))
                        );
            }

            // 인기순 / 추천순
            // 찜 개수가 낮은 카드 조회
            // 찜 개수가 같으면 id가 높은 카드 조회
            case POPULAR, RECOMMENDED -> {
                Integer likeCount;

                try {
                    likeCount = Integer.parseInt(cardCursor.value());
                } catch (NumberFormatException e) {
                    throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
                }

                yield qCard.likeCard.lt(likeCount)
                        .or(
                                qCard.likeCard.eq(likeCount)
                                        .and(qCard.id.gt(cardCursor.cardId()))
                        );
            }

            // 최신순
            // 생성일이 이전인 카드 조회
            // 생성일이 같으면 id가 높은 카드 조회
            case LATEST -> {

                LocalDateTime createdAt;

                try {
                    createdAt = LocalDateTime.parse(cardCursor.value());
                } catch (DateTimeParseException e) {
                    throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
                }

                yield qCard.createdAt.lt(createdAt)
                        .or(
                                qCard.createdAt.eq(createdAt)
                                        .and(qCard.id.gt(cardCursor.cardId()))
                        );
            }
        };
    }

    // 아트 타입 필터
    private BooleanExpression artTypeCondition(ArtType artType) {
        return artType == null ? null : qCard.artType.eq(artType);
    }

    // 가격 필터(카드의 가격 범위와 사용자가 원하는 가격 범위가 겹치는 카드를 찾음)
    private BooleanExpression priceCondition(CardSearchRequest request) {

        BooleanExpression condition = null;

        if (request.minPrice() != null) {
            condition = qCard.maxPrice.goe(request.minPrice());
        }

        if (request.maxPrice() != null) {
            BooleanExpression maxCondition = qCard.minPrice.loe(request.maxPrice());
            condition = condition == null ? maxCondition : condition.and(maxCondition);
        }
        return condition;
    }

    // 지역 조건
    private BooleanExpression regionCondition(List<Long> regionIds) {
        return regionIds == null || regionIds.isEmpty() ? null : qCard.shop.region.id.in(regionIds);
    }

    // 디자인 태그 조건 (EXISTS 서브쿼리로 변경 — 컬렉션 조인으로 인한 행 중복/카운트 부풀림 방지)
    private BooleanExpression designTagCondition(List<Long> designTagIds) {
        if (designTagIds == null || designTagIds.isEmpty()) {
            return null;
        }

        QCardDesignTag sub = new QCardDesignTag("cardDesignTagSub");

        return JPAExpressions
                .selectOne()
                .from(sub)
                .where(
                        sub.card.eq(qCard),
                        sub.designTag.id.in(designTagIds)
                )
                .exists();
    }

    // 정렬
    private OrderSpecifier<?>[] getOrder(SortType sort) {
        if (sort == null) {
            return new OrderSpecifier[]{
                    qCard.likeCard.desc(),
                    qCard.id.asc()
            };
        }

        return switch (sort) {

            // 추천/인기: 찜많은순, 추천순은 추후 구현 예정
            case RECOMMENDED, POPULAR -> new OrderSpecifier[]{
                    qCard.likeCard.desc(),
                    qCard.id.asc()
            };

            // 최신 순
            case LATEST -> new OrderSpecifier[]{
                    qCard.createdAt.desc(),
                    qCard.id.asc()
            };

            // 가격 낮은 순
            case PRICE_ASC -> new OrderSpecifier[]{
                    qCard.minPrice.asc(),
                    qCard.maxPrice.asc(),
                    qCard.id.asc()
            };

            // 가격 높은 순
            case PRICE_DESC -> new OrderSpecifier[]{
                    qCard.minPrice.desc(),
                    qCard.maxPrice.desc(),
                    qCard.id.asc()
            };
        };
    }
}
