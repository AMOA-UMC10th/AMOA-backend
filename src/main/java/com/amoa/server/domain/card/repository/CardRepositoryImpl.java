package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.entity.QCard;
import com.amoa.server.domain.card.entity.mapping.QCardDesignTag;
import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.SortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
                        designTagCondition(request.designTagId())
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
                        cursorCondition(request.cursor()),                   // 커서 페이지네이션
                        artTypeCondition(request.artType()),
                        priceCondition(request),
                        regionCondition(request.regionIds()),
                        designTagCondition(request.designTagId())
                )
                .orderBy(getOrder(request.sort()))                           // 정렬
                .limit(size + 1)
                .fetch();
    }


    // 커서 페이지네이션(첫 페이지는 커서 없음)
    private BooleanExpression cursorCondition(Long cursor) {
        return cursor == null ? null : qCard.id.lt(cursor);
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

    // 디자인 태그 조건
    private BooleanExpression designTagCondition(Long designTagId) {
        return designTagId == null ? null : qCard.cardDesignTags.any().designTag.id.eq(designTagId);
    }

    // 정렬
    private OrderSpecifier<?>[] getOrder(SortType sort) {
        if (sort == null) {
            return new OrderSpecifier[]{
                    qCard.createdAt.desc(),
                    qCard.id.desc()
            };
        }

        return switch (sort) {

            // 추천/인기: 찜많은순, 추천순은 추후 구현 예정
            case RECOMMENDED, POPULAR -> new OrderSpecifier[]{
                    qCard.likeCard.desc(),
                    qCard.id.desc()
            };

            // 최신 순
            case LATEST -> new OrderSpecifier[]{
                    qCard.createdAt.desc(),
                    qCard.id.desc()
            };

            // 가격 낮은 순
            case PRICE_ASC -> new OrderSpecifier[]{
                    qCard.minPrice.asc(),
                    qCard.id.desc()
            };

            // 가격 높은 순
            case PRICE_DESC -> new OrderSpecifier[]{
                    qCard.maxPrice.desc(),
                    qCard.id.desc()
            };
        };
    }
}
