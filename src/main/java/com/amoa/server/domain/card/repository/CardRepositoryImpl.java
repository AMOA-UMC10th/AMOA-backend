package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.dto.request.CardReqDTO.ShopCardSearchRequest;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.entity.QCard;
import com.amoa.server.domain.card.entity.mapping.QCardDesignTag;
import com.amoa.server.domain.card.exception.CardException;
import com.amoa.server.domain.card.exception.code.CardErrorCode;
import com.amoa.server.domain.card.util.CardCursor;
import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.MonthlyPeriod;
import com.amoa.server.domain.common.enums.SortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CardRepositoryImpl implements CardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QCard qCard = QCard.card;
    private final EntityManager entityManager;
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
                        designTagCondition(request.designTagIds()),
                        monthlyPeriodCondition(request.artType(), request.period())
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
                        designTagCondition(request.designTagIds()),
                        monthlyPeriodCondition(request.artType(), request.period())
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
                .and(designTagCondition(narrowerRequest.designTagIds()))
                .and(monthlyPeriodCondition(narrowerRequest.artType(), narrowerRequest.period()));

        return queryFactory
                .select(qCard.count())
                .from(qCard)
                .where(
                        qCard.deletedAt.isNull(),
                        artTypeCondition(broaderRequest.artType()),
                        priceCondition(broaderRequest),
                        regionCondition(broaderRequest.regionIds()),
                        designTagCondition(broaderRequest.designTagIds()),
                        monthlyPeriodCondition(broaderRequest.artType(), broaderRequest.period()),
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

    // MONTHLY 아트의 기간 조건 (artType=MONTHLY)
    private BooleanExpression monthlyPeriodCondition(
            ArtType artType,
            MonthlyPeriod period
    ) {
        if (artType != ArtType.MONTHLY || period == null) {
            return null;
        }

        LocalDate currentMonth = YearMonth.now().atDay(1);

        return switch (period) {
            case CURRENT -> qCard.createdMonth.eq(currentMonth);
            case PAST -> qCard.createdMonth.lt(currentMonth);
        };
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

    private static final String MOOD_CURSOR_PREFIX = "M:";
    private static final String STANDARD_CURSOR_PREFIX = "S:";

    // 샵 상세 페이지에서의 하단 카드 목록, 샵 상세 페이지를 처음 집입했을 때 정렬 기준으로는 RECOMMENDED가 디폴트
    @Override
    public List<Card> findShopCards(ShopCardSearchRequest request, int size) {
        String cursor = request.cursor();
        boolean hasPreferredTags = request.preferredDesignTagIds() != null
                && !request.preferredDesignTagIds().isEmpty();

        boolean cursorIsMood = cursor != null && cursor.startsWith(MOOD_CURSOR_PREFIX);
        boolean cursorIsStandard = cursor != null && cursor.startsWith(STANDARD_CURSOR_PREFIX);

        if (cursor != null && !cursorIsMood && !cursorIsStandard) {
            throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
        }

        // 첫 페이지(커서 없음)는 지금 요청 기준으로 모드 결정, 이후 페이지는 커서에 적힌 모드를 그대로 따름
        boolean useMoodPriority = cursor == null
                ? request.sort() == SortType.RECOMMENDED && hasPreferredTags
                : cursorIsMood;

        // 커서는 무드모드인데 지금은 무드 태그가 없음(온보딩 변경 등) -> 조용히 잘못 해석하지 말고 명확히 에러
        if (useMoodPriority && !hasPreferredTags) {
            throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
        }

        String cursorBody = cursor == null ? null : cursor.substring(2); // "M:" / "S:" 제거

        NumberExpression<Integer> moodPriority = useMoodPriority
                ? moodPriorityExpression(request.preferredDesignTagIds())
                : null;

        return queryFactory
                .selectFrom(qCard)
                .join(qCard.shop).fetchJoin()
                .join(qCard.shop.region).fetchJoin()
                .where(
                        qCard.deletedAt.isNull(),
                        qCard.shop.id.eq(request.shopId()),
                        artTypeCondition(request.artType()),
                        useMoodPriority
                                ? moodCursorCondition(cursorBody, moodPriority)
                                : cursorCondition(cursorBody, request.sort())
                )
                .orderBy(useMoodPriority
                        ? moodPriorityOrder(moodPriority)
                        : getOrder(request.sort()))
                .limit(size + 1)
                .fetch();
    }

    // 샵 상세 조회 페이지에서 보여지는 카드의 개수
    @Override
    public Long countShopCards(Long shopId, ArtType artType) {
        return queryFactory
                .select(qCard.count())
                .from(qCard)
                .where(
                        qCard.deletedAt.isNull(),
                        qCard.shop.id.eq(shopId),
                        artTypeCondition(artType)
                )
                .fetchOne();
    }

    // 온보딩 관심 디자인무드 매칭 여부를 0/1 우선순위로 변환 (0 -> 매칭, 1 -> 매칭X)
    private NumberExpression<Integer> moodPriorityExpression(List<Long> preferredDesignTagIds) {
        QCardDesignTag sub = new QCardDesignTag("cardDesignTagMoodSub");

        BooleanExpression matchesPreferredMood = JPAExpressions
                .selectOne()
                .from(sub)
                .where(
                        sub.card.eq(qCard),
                        sub.designTag.id.in(preferredDesignTagIds)
                )
                .exists();

        return new CaseBuilder()
                .when(matchesPreferredMood).then(0)
                .otherwise(1);
    }

    // 무드 매칭 우선 → 찜 많은 순 → id
    private OrderSpecifier<?>[] moodPriorityOrder(NumberExpression<Integer> moodPriority) {
        return new OrderSpecifier[]{
                moodPriority.asc(),  // 0(매칭)이 먼저, 1(안매칭)이 나중
                qCard.likeCard.desc(), // 찜 많은 순
                qCard.id.asc() // id
        };
    }

    // 무드 우선순위 기반 커서 조건 (priority_likeCard_id 3단 커서, PRICE_ASC 패턴과 동일한 방식)
    private BooleanExpression moodCursorCondition(String cursor, NumberExpression<Integer> moodPriority) {
        if (cursor == null) {
            return null;
        }

        CardCursor cardCursor = CardCursor.from(cursor);

        Integer priority;
        Integer likeCount;

        try {
            if (cardCursor.secondValue() == null) {
                throw new NumberFormatException();
            }
            priority = Integer.parseInt(cardCursor.value());
            likeCount = Integer.parseInt(cardCursor.secondValue());
        } catch (NumberFormatException | NullPointerException e) {
            throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
        }

        return moodPriority.gt(priority)
                .or(
                        moodPriority.eq(priority)
                                .and(qCard.likeCard.lt(likeCount))
                )
                .or(
                        moodPriority.eq(priority)
                                .and(qCard.likeCard.eq(likeCount))
                                .and(qCard.id.gt(cardCursor.cardId()))
                );
    }

    @Override
    public List<Card> findRecommendedCards(Card currentCard, int limit) {

        QCard qCard = QCard.card;
        QCardDesignTag existsSub = new QCardDesignTag("cardDesignTagExists");
        QCardDesignTag countSub = new QCardDesignTag("cardDesignTagCount");

        String firstDepth = currentCard.getShop().getRegion().getFirstDepth();
        String secondDepth = currentCard.getShop().getRegion().getSecondDepth();
        String thirdDepth = currentCard.getShop().getRegion().getThirdDepth();

        List<Long> currentTagIds = currentCard.getCardDesignTags().stream()
                .map(cardDesignTag -> cardDesignTag.getDesignTag().getId())
                .toList();

        // 현재 카드에 태그가 없으면 "1개 이상 겹침" 조건을 만족할 수 없음
        if (currentTagIds.isEmpty()) {
            return List.of();
        }

        // 지역 우선순위: 1depth부터 순서대로 몇 단계까지 일치하는지
        NumberExpression<Integer> locationTier = new CaseBuilder()
                .when(qCard.shop.region.firstDepth.eq(firstDepth)
                        .and(qCard.shop.region.secondDepth.eq(secondDepth))
                        .and(qCard.shop.region.thirdDepth.eq(thirdDepth)))
                .then(1)
                .when(qCard.shop.region.firstDepth.eq(firstDepth)
                        .and(qCard.shop.region.secondDepth.eq(secondDepth)))
                .then(2)
                .otherwise(3); // firstDepth만 일치 (where절에서 firstDepth 불일치는 이미 제외됨)

        // 디자인 태그 겹침 개수 (정렬용)
        NumberExpression<Long> tagMatchCount = Expressions.asNumber(
                JPAExpressions
                        .select(countSub.count())
                        .from(countSub)
                        .where(
                                countSub.card.eq(qCard),
                                countSub.designTag.id.in(currentTagIds)
                        )
        );

        return queryFactory
                .selectFrom(qCard)
                .join(qCard.shop).fetchJoin()
                .join(qCard.shop.region).fetchJoin()
                .where(
                        qCard.deletedAt.isNull(),
                        qCard.id.ne(currentCard.getId()),
                        qCard.shop.region.firstDepth.eq(firstDepth),      // 지역 최소 조건
                        JPAExpressions                                    // 태그 최소 조건
                                .selectOne()
                                .from(existsSub)
                                .where(
                                        existsSub.card.eq(qCard),
                                        existsSub.designTag.id.in(currentTagIds)
                                )
                                .exists()
                )
                .orderBy(
                        locationTier.asc(),
                        tagMatchCount.desc(),
                        qCard.likeCard.desc(),
                        qCard.id.asc()
                )
                .limit(limit)
                .fetch();
    }

    // stage1 카드 중, 특정 샵이 targetCount개 이상을 차지하는 샵 ID 목록 반환 (완전 포화 샵 판별용)
    private List<Long> findShopIdsWithCardCount(List<Long> stage1Ids, int targetCount) {
        if (stage1Ids.isEmpty()) {
            return List.of();
        }

        List<Card> stage1Cards = findCardsByIdsWithShopAndRegion(stage1Ids);

        return stage1Cards.stream()
                .collect(Collectors.groupingBy(card -> card.getShop().getId(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() >= targetCount)
                .map(Map.Entry::getKey)
                .toList();
    }

    // 이달의 아트 Stage 1: 온보딩 지역/태그 조건에 맞는 카드 ID를, 샵당 최대 2개로 제한해 찜순으로 조회
    @SuppressWarnings("unchecked")
    private List<Long> findMonthlyArtStage1Ids(
            LocalDate monthStart,
            LocalDate monthEnd,
            List<Long> regionIds,
            List<Long> designTagIds,
            int limit
    ) {
        String sql = """
                SELECT ranked.card_id AS card_id
                FROM (
                    SELECT
                        c.card_id AS card_id,
                        c.like_card AS like_card,
                        ROW_NUMBER() OVER (
                            PARTITION BY c.shop_id
                            ORDER BY c.like_card DESC, c.card_id ASC
                        ) AS shop_rank
                    FROM card c
                    JOIN shop s ON c.shop_id = s.shop_id
                    WHERE c.deleted_at IS NULL
                      AND c.art_type = 'MONTHLY'
                      AND c.created_month BETWEEN :monthStart AND :monthEnd
                      AND s.region_id IN (:regionIds)
                      AND EXISTS (
                            SELECT 1 FROM card_design_tag cdt
                            WHERE cdt.card_id = c.card_id
                              AND cdt.design_tag_id IN (:designTagIds)
                      )
                ) ranked
                WHERE ranked.shop_rank <= 2
                ORDER BY ranked.like_card DESC, ranked.card_id ASC
                LIMIT :limit
                """;

        List<Object> rows = entityManager.createNativeQuery(sql)
                .setParameter("monthStart", monthStart)
                .setParameter("monthEnd", monthEnd)
                .setParameter("regionIds", regionIds)
                .setParameter("designTagIds", designTagIds)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream().map(row -> ((Number) row).longValue()).toList();
    }

    // 이달의 아트 Stage 2: 온보딩 조건 없이, 이미 뽑힌 카드/포화된 샵을 제외하고 채움
    @SuppressWarnings("unchecked")
    private List<Long> findMonthlyArtStage2Ids(
            LocalDate monthStart,
            LocalDate monthEnd,
            List<Long> excludeIds,
            List<Long> saturatedShopIds,
            int remainingPerShop,
            int limit
    ) {
        String excludeIdsSql = excludeIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String saturatedShopSql = saturatedShopIds.isEmpty()
                ? "-1"
                : saturatedShopIds.stream().map(id -> "?").collect(Collectors.joining(","));

        String sql = """
                SELECT ranked.card_id AS card_id
                FROM (
                    SELECT
                        c.card_id AS card_id,
                        c.like_card AS like_card,
                        ROW_NUMBER() OVER (
                            PARTITION BY c.shop_id
                            ORDER BY c.like_card DESC, c.card_id ASC
                        ) AS shop_rank
                    FROM card c
                    WHERE c.deleted_at IS NULL
                      AND c.art_type = 'MONTHLY'
                      AND c.created_month BETWEEN ? AND ?
                      AND c.card_id NOT IN (%s)
                      AND c.shop_id NOT IN (%s)
                ) ranked
                WHERE ranked.shop_rank <= ?
                ORDER BY ranked.like_card DESC, ranked.card_id ASC
                LIMIT ?
                """.formatted(excludeIdsSql, saturatedShopSql);

        jakarta.persistence.Query query = entityManager.createNativeQuery(sql);

        int idx = 1;
        query.setParameter(idx++, monthStart);
        query.setParameter(idx++, monthEnd);
        for (Long id : excludeIds) {
            query.setParameter(idx++, id);
        }
        for (Long shopId : saturatedShopIds) {
            query.setParameter(idx++, shopId);
        }
        query.setParameter(idx++, remainingPerShop);
        query.setParameter(idx, limit);

        List<Object> rows = query.getResultList();
        return rows.stream().map(row -> ((Number) row).longValue()).toList();
    }

    // 완벽한 연말을 위한 PICK: 샵당 최대 2개 제한된 카드 ID를 찜순으로 조회
    @SuppressWarnings("unchecked")
    private List<Long> findYearEndPickIds(int limit) {
        String sql = """
                SELECT ranked.card_id AS card_id
                FROM (
                    SELECT
                        c.card_id AS card_id,
                        c.like_card AS like_card,
                        ROW_NUMBER() OVER (
                            PARTITION BY c.shop_id
                            ORDER BY c.like_card DESC, c.card_id ASC
                        ) AS shop_rank
                    FROM card c
                    WHERE c.deleted_at IS NULL
                ) ranked
                WHERE ranked.shop_rank <= 2
                ORDER BY ranked.like_card DESC, ranked.card_id ASC
                LIMIT :limit
                """;

        List<Object> rows = entityManager.createNativeQuery(sql)
                .setParameter("limit", limit)
                .getResultList();

        return rows.stream().map(row -> ((Number) row).longValue()).toList();
    }

    // ID 목록을 받아 shop/region까지 fetch join으로 한 번에 조회 (N+1 방지), ID 순서를 그대로 보존
    private List<Card> findCardsByIdsWithShopAndRegion(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        List<Card> cards = queryFactory
                .selectFrom(qCard)
                .join(qCard.shop).fetchJoin()
                .join(qCard.shop.region).fetchJoin()
                .where(qCard.id.in(ids))
                .fetch();

        Map<Long, Integer> orderIndex = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            orderIndex.put(ids.get(i), i);
        }

        return cards.stream()
                .sorted(Comparator.comparingInt(card -> orderIndex.get(card.getId())))
                .toList();
    }

    // 정렬 순서를 유지하며 샵당 maxPerShop개로 최종 제한 (stage1+stage2 합산 후 안전망)
    private List<Card> limitPerShop(List<Card> cards, int maxPerShop, int limit) {
        Map<Long, Integer> shopCount = new HashMap<>();
        List<Card> result = new ArrayList<>();

        for (Card card : cards) {
            Long shopId = card.getShop().getId();
            int count = shopCount.getOrDefault(shopId, 0);

            if (count >= maxPerShop) {
                continue;
            }

            result.add(card);
            shopCount.put(shopId, count + 1);

            if (result.size() >= limit) {
                break;
            }
        }

        return result;
    }

    @Override
    public List<Card> findMonthlyArtCards(
            List<Long> onboardingRegionIds,
            List<Long> onboardingDesignTagIds,
            int limit
    ) {
        YearMonth now = YearMonth.now();
        LocalDate monthStart = now.atDay(1);
        LocalDate monthEnd = now.atEndOfMonth();

        boolean hasRegion = onboardingRegionIds != null && !onboardingRegionIds.isEmpty();
        boolean hasTag = onboardingDesignTagIds != null && !onboardingDesignTagIds.isEmpty();

        if (!hasRegion || !hasTag) {
            List<Long> ids = findMonthlyArtStage2Ids(
                    monthStart, monthEnd, List.of(-1L), List.of(), 2, limit
            );
            return findCardsByIdsWithShopAndRegion(ids);
        }

        // Stage 1: 온보딩 조건 + 샵당 2개 제한
        List<Long> stage1Ids = findMonthlyArtStage1Ids(
                monthStart, monthEnd, onboardingRegionIds, onboardingDesignTagIds, limit
        );

        if (stage1Ids.size() >= limit) {
            return findCardsByIdsWithShopAndRegion(stage1Ids);
        }

        // 샵당 2개를 이미 채운(포화된) 샵은 Stage 2에서 제외
        List<Long> saturatedShopIds = findShopIdsWithCardCount(stage1Ids, 2);

        List<Long> excludeIds = stage1Ids.isEmpty() ? List.of(-1L) : stage1Ids;
        int remaining = limit - stage1Ids.size();

        List<Long> stage2Ids = findMonthlyArtStage2Ids(
                monthStart, monthEnd, excludeIds, saturatedShopIds, 2, remaining * 3
        );

        List<Long> combinedIds = new ArrayList<>(stage1Ids);
        combinedIds.addAll(stage2Ids);

        List<Card> combinedCards = findCardsByIdsWithShopAndRegion(combinedIds);

        // 부분 포화 샵(1개만 쓴 샵) 케이스에 대한 최종 안전망
        return limitPerShop(combinedCards, 2, limit);
    }

    // 완벽한 연말을 위한 PICK: 기준 미정, 임시로 인기순 top N + 샵당 최대 2개 제한
    @Override
    public List<Card> findYearEndPickCards(int limit) {
        List<Long> ids = findYearEndPickIds(limit);
        return findCardsByIdsWithShopAndRegion(ids);
    }
}
