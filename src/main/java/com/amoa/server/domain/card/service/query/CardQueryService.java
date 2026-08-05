package com.amoa.server.domain.card.service.query;

import com.amoa.server.domain.card.converter.CardConverter;
import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.dto.response.CardResDTO;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.exception.CardException;
import com.amoa.server.domain.card.exception.code.CardErrorCode;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.card.repository.UserCardRepository;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.user.repository.UserDesignTagRepository;
import com.amoa.server.domain.user.repository.UserInterestedRegionRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardQueryService {

    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final UserDesignTagRepository userDesignTagRepository;
    private final UserInterestedRegionRepository userInterestedRegionRepository;
    private final CardConverter cardConverter;

    // 카카오로 시작하기
    @Transactional(readOnly = true)
    public CardResDTO.KakaoChannel getKakaoChannel(Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CardException(CardErrorCode.CARD_NOT_FOUND)
                );

        return new CardResDTO.KakaoChannel(
                card.getShop().getKakaoChannelUrl()
        );
    }

    // 아트 목록 조회
    @Transactional(readOnly = true)
    public CardResDTO.CardList searchCards(
            CardSearchRequest request,
            Long userId
    ) {
        // 추천순인 경우 2단계 조회
        if (request.sort() == null || request.sort() == SortType.RECOMMENDED) {
            return searchCardsWithRecommendation(request, userId);
        }

        // 추천순이 아니면 기존 로직
        return searchCardsDefault(request, userId);
    }

    /**
     * 추천순: 2단계 조회 온보딩이 없으면 일반 조회, 온보딩이 있으면 2단계 조회
     */
    private CardResDTO.CardList searchCardsWithRecommendation(
            CardSearchRequest request,
            Long userId
    ) {
        List<Long> onboardingRegionIds = getOnboardingRegionIds(userId);
        List<Long> onboardingDesignTagIds = getOnboardingDesignTagIds(userId);

        boolean hasOnboardingRegion = !onboardingRegionIds.isEmpty();
        boolean hasOnboardingDesignTag = !onboardingDesignTagIds.isEmpty();

        if (!hasOnboardingRegion && !hasOnboardingDesignTag) {
            return searchCardsDefault(request, userId);
        }

        boolean hasRegionSearch = request.regionIds() != null && !request.regionIds().isEmpty();
        boolean hasDesignTagSearch = request.designTagIds() != null && !request.designTagIds().isEmpty();

        int size = request.size() == null ? 20 : Math.max(1, Math.min(request.size(), 100));

        // 커서 상태 디코딩: stage2로 넘어간 이후에는 stage1 커서를 절대 적용하지 않음
        TwoStageCursor cursorState = TwoStageCursor.decode(request.cursor());
        String stage1Cursor = cursorState.stage() == 1 ? cursorState.innerCursor() : null;

        if (hasRegionSearch && hasDesignTagSearch) {
            return searchCardsDefault(request, userId);
        }

        if (hasRegionSearch && !hasDesignTagSearch) {
            if (!hasOnboardingDesignTag) {
                return searchCardsDefault(request, userId);
            }

            CardSearchRequest stage1Request = new CardSearchRequest(
                    request.regionIds(),
                    request.minPrice(),
                    request.maxPrice(),
                    request.artType(),
                    onboardingDesignTagIds,
                    SortType.RECOMMENDED,
                    stage1Cursor,
                    request.size(),
                    request.period()
            );

            return handleTwoStageSearch(stage1Request, request, userId, size, true, false,
                    onboardingRegionIds, onboardingDesignTagIds, cursorState);
        }

        if (!hasRegionSearch && hasDesignTagSearch) {
            if (!hasOnboardingRegion) {
                return searchCardsDefault(request, userId);
            }

            CardSearchRequest stage1Request = new CardSearchRequest(
                    onboardingRegionIds,
                    request.minPrice(),
                    request.maxPrice(),
                    request.artType(),
                    request.designTagIds(),
                    SortType.RECOMMENDED,
                    stage1Cursor,
                    request.size(),
                    request.period()
            );

            return handleTwoStageSearch(stage1Request, request, userId, size, false, true,
                    onboardingRegionIds, onboardingDesignTagIds, cursorState);
        }

        CardSearchRequest stage1Request = new CardSearchRequest(
                onboardingRegionIds.isEmpty() ? null : onboardingRegionIds,
                request.minPrice(),
                request.maxPrice(),
                request.artType(),
                onboardingDesignTagIds.isEmpty() ? null : onboardingDesignTagIds,
                SortType.RECOMMENDED,
                stage1Cursor,
                request.size(),
                request.period()
        );

        return handleTwoStageSearch(stage1Request, request, userId, size, false, false,
                onboardingRegionIds, onboardingDesignTagIds, cursorState);
    }

    /**
     * 2단계 조회 공통 로직
     */
    private CardResDTO.CardList handleTwoStageSearch(
            CardSearchRequest stage1Request,
            CardSearchRequest originalRequest,
            Long userId,
            int size,
            boolean isRegionSearchOnly,
            boolean isDesignTagSearchOnly,
            List<Long> onboardingRegionIds,
            List<Long> onboardingDesignTagIds,
            TwoStageCursor cursorState
    ) {
        // stage2로 이미 넘어간 상태면 stage1은 소진된 것으로 보고 재조회하지 않음
        boolean resumingStage2 = cursorState.stage() == 2;

        List<Card> stage1Cards = resumingStage2
                ? Collections.emptyList()
                : cardRepository.findCards(stage1Request, size);
        Long stage1TotalCount = cardRepository.countCards(stage1Request);

        if (!resumingStage2 && stage1Cards.size() > size) {
            List<Card> finalCards = stage1Cards.subList(0, size);
            Set<Long> likedCardIds = getLikedCardIds(userId, finalCards);
            List<CardResDTO.CardInfo> cardInfos = finalCards.stream()
                    .map(card -> cardConverter.toCardInfo(card, likedCardIds))
                    .toList();

            String nextCursor = TwoStageCursor.encode(
                    1,
                    createCursor(finalCards.get(finalCards.size() - 1), SortType.RECOMMENDED)
            );

            return new CardResDTO.CardList(
                    stage1TotalCount,
                    cardInfos.size(),
                    cardInfos,
                    nextCursor,
                    true
            );
        }

        int remainingSize = resumingStage2 ? size : size - stage1Cards.size();

        CardSearchRequest stage2Request;
        String stage2Cursor = resumingStage2 ? cursorState.innerCursor() : null;

        if (isRegionSearchOnly) {
            stage2Request = new CardSearchRequest(
                    originalRequest.regionIds(),
                    originalRequest.minPrice(),
                    originalRequest.maxPrice(),
                    originalRequest.artType(),
                    null,
                    SortType.RECOMMENDED,
                    stage2Cursor,
                    remainingSize + size,
                    originalRequest.period()
            );
        } else if (isDesignTagSearchOnly) {
            stage2Request = new CardSearchRequest(
                    null,
                    originalRequest.minPrice(),
                    originalRequest.maxPrice(),
                    originalRequest.artType(),
                    originalRequest.designTagIds(),
                    SortType.RECOMMENDED,
                    stage2Cursor,
                    remainingSize + size,
                    originalRequest.period()
            );
        } else {
            stage2Request = new CardSearchRequest(
                    null,
                    originalRequest.minPrice(),
                    originalRequest.maxPrice(),
                    originalRequest.artType(),
                    null,
                    SortType.RECOMMENDED,
                    stage2Cursor,
                    remainingSize + size,
                    originalRequest.period()
            );
        }

        // stage1과 겹치는 카드(최대 stage1Cards.size()개, size 이하)를 필터링하고도
        // remainingSize + 1개를 확보할 수 있도록 여유 있게 fetch
        int stage2FetchLimit = remainingSize + size;
        List<Card> stage2Cards = cardRepository.findCards(stage2Request, stage2FetchLimit);

        Set<Long> stage1CardIds = stage1Cards.stream()
                .map(Card::getId)
                .collect(Collectors.toSet());

        // ID 기반 제외 대신, stage1의 전체 필터 조건을 배제 조건으로 사용
        // (현재 페이지의 stage1Cards 유무·resumingStage2 여부와 무관하게 항상 정확)
        Long stage2TotalCount = cardRepository.countCardsExcludingConditions(stage2Request, stage1Request);

        List<Card> filteredStage2Cards = stage2Cards.stream()
                .filter(card -> !stage1CardIds.contains(card.getId()))
                .limit(remainingSize + 1)
                .collect(Collectors.toList());

        boolean stage2HasMore = filteredStage2Cards.size() > remainingSize;

        Card stage2LastCard = null;

        if (stage2HasMore) {
            // remainingSize == 0인 경우(stage1이 정확히 size개로 소진) get(-1) 방지
            stage2LastCard = remainingSize > 0
                    ? filteredStage2Cards.get(remainingSize - 1)
                    : null;
            filteredStage2Cards = filteredStage2Cards.subList(0, remainingSize);
        }

        List<Card> allCards = new ArrayList<>(stage1Cards);
        allCards.addAll(filteredStage2Cards);

        Set<Long> likedCardIds = getLikedCardIds(userId, allCards);
        List<CardResDTO.CardInfo> cardInfos = allCards.stream()
                .map(card -> cardConverter.toCardInfo(card, likedCardIds))
                .toList();

        String nextCursor = null;
        boolean hasNext = false;

        if (stage2HasMore) {
            hasNext = true;
            // stage2LastCard가 null이면(remainingSize==0) stage2를 처음부터 다시 조회하도록 innerCursor=null로 인코딩
            nextCursor = TwoStageCursor.encode(
                    2,
                    stage2LastCard == null ? null : createCursor(stage2LastCard, SortType.RECOMMENDED)
            );
        }

        Long totalCount = stage1TotalCount + stage2TotalCount;

        return new CardResDTO.CardList(
                totalCount,
                cardInfos.size(),
                cardInfos,
                nextCursor,
                hasNext
        );
    }

    /**
     * 기존 조회 로직 (추천순 제외)
     */
    private CardResDTO.CardList searchCardsDefault(
            CardSearchRequest request,
            Long userId
    ) {
        // 전체 개수 조회(검색 결과가 총 몇 개인지)
        Long totalCount = cardRepository.countCards(request);

        // size 기본값 20, 최소 1, 최대 100으로 제한
        int size = request.size() == null
                ? 20
                : Math.max(1, Math.min(request.size(), 100));

        // 현재 페이지 조회
        List<Card> cards = cardRepository.findCards(request, size);

        // 커서 페이지네이션
        boolean hasNext = cards.size() > size;
        if (hasNext) {
            cards = cards.subList(0, size);
        }

        // 찜 여부 조회
        Set<Long> likedCardIds = getLikedCardIds(userId, cards);

        //Entity -> DTO
        List<CardResDTO.CardInfo> cardInfos = cards.stream()
                .map(card -> cardConverter.toCardInfo(card, likedCardIds))
                .toList();

        // 다음 커서 생성(복합 커서)
        String nextCursor = null;

        if (hasNext) {
            Card lastCard = cards.get(cards.size() - 1);

            SortType sort = request.sort() == null
                    ? SortType.RECOMMENDED
                    : request.sort();

            nextCursor = createCursor(lastCard, sort);
        }

        // 응답 DTO 생성
        return new CardResDTO.CardList(
                totalCount,
                cardInfos.size(),
                cardInfos,
                nextCursor,
                hasNext
        );
    }

    /**
     * 커서 생성 헬퍼 메서드
     */
    private String createCursor(Card card, SortType sort) {
        return switch (sort) {
            case PRICE_ASC -> card.getMinPrice() + "_" + card.getMaxPrice() + "_" + card.getId();
            case PRICE_DESC -> card.getMinPrice() + "_" + card.getMaxPrice() + "_" + card.getId();
            case POPULAR, RECOMMENDED -> card.getLikeCard() + "_" + card.getId();
            case LATEST -> card.getCreatedAt() + "_" + card.getId();
        };
    }

    /**
     * 2단계 조회 커서 상태 인코딩 어느 단계(stage 1 / stage 2)에서 페이지네이션 중인지 커서에 함께 저장하여, stage-2 커서가 stage-1 쿼리에 잘못 적용되는 것을 방지한다.
     */
    private record TwoStageCursor(int stage, String innerCursor) {

        private static final String STAGE1_PREFIX = "S1:";
        private static final String STAGE2_PREFIX = "S2:";

        static TwoStageCursor decode(String cursor) {
            if (cursor == null) {
                return new TwoStageCursor(1, null);
            }
            if (cursor.startsWith(STAGE2_PREFIX)) {
                String inner = cursor.substring(STAGE2_PREFIX.length());
                return new TwoStageCursor(2, inner.isEmpty() ? null : inner);
            }
            if (cursor.startsWith(STAGE1_PREFIX)) {
                String inner = cursor.substring(STAGE1_PREFIX.length());
                return new TwoStageCursor(1, inner.isEmpty() ? null : inner);
            }
            // prefix 없는 커서(단일 단계 흐름 등 하위호환)는 stage1로 취급
            return new TwoStageCursor(1, cursor);
        }

        static String encode(int stage, String innerCursor) {
            String inner = innerCursor == null ? "" : innerCursor;
            return (stage == 2 ? STAGE2_PREFIX : STAGE1_PREFIX) + inner;
        }
    }


    /**
     * 사용자의 온보딩 관심 지역 ID 조회 (최대 3개)
     */
    private List<Long> getOnboardingRegionIds(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        return userInterestedRegionRepository.findAllByUser_Id(userId)
                .stream()
                .map(userRegion -> userRegion.getRegion().getId())
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 온보딩 디자인 무드 태그 ID 조회
     */
    private List<Long> getOnboardingDesignTagIds(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        return userDesignTagRepository.findAllByUser_Id(userId)
                .stream()
                .map(userDesignTag -> userDesignTag.getDesignTag().getId())
                .collect(Collectors.toList());
    }

    // 로그인 사용자가 찜한 카드 ID 조회
    private Set<Long> getLikedCardIds(Long userId, List<Card> cards) {

        // 비로그인 사용자, 카드가 0개 이면 찜 여부 조회하지 않음
        if (userId == null || cards.isEmpty()) {
            return Collections.emptySet();
        }

        // 현재 화면 카드 id만 추출
        List<Long> cardIds = cards.stream()
                .map(Card::getId)
                .toList();

        // UserCard 조회 후 카드 id를 Set으로 반환
        return userCardRepository.findByUserIdAndCardIdIn(userId, cardIds)
                .stream()
                .map(userCard -> userCard.getCard().getId())
                .collect(Collectors.toSet());
    }

    // 아트 추천 목록 조회
    @Transactional(readOnly = true)
    public CardResDTO.RecommendedCardList getRecommendedCards(Long cardId, Long userId) {

        Card currentCard = cardRepository.findByIdWithShopRegionAndTags(cardId)
                .orElseThrow(() -> new CardException(CardErrorCode.CARD_NOT_FOUND));

        List<Card> recommended = cardRepository.findRecommendedCards(currentCard, 6);

        Set<Long> likedCardIds = getLikedCardIds(userId, recommended);

        List<CardResDTO.CardInfo> cardInfos = recommended.stream()
                .map(card -> cardConverter.toCardInfo(card, likedCardIds))
                .toList();

        return new CardResDTO.RecommendedCardList(cardInfos);
    }

    // 아트 상세 조회
    @Transactional(readOnly = true)
    public CardResDTO.CardDetailResponse getCardDetail(Long cardId) {

        Card card = cardRepository.findDetailById(cardId)
                .orElseThrow(() ->
                        new CardException(CardErrorCode.CARD_NOT_FOUND)
                );

        return cardConverter.toCardDetail(card);
    }

    // 홈 화면 카드 섹션 조회
    @Transactional(readOnly = true)
    public CardResDTO.HomeSections getHomeSections(Long userId, String nickname) {

        List<Long> onboardingRegionIds = getOnboardingRegionIds(userId);
        List<Long> onboardingDesignTagIds = getOnboardingDesignTagIds(userId);

        List<Card> monthlyArtCards = cardRepository.findMonthlyArtCards(
                onboardingRegionIds, onboardingDesignTagIds, 6
        );
        List<Card> yearEndPickCards = cardRepository.findYearEndPickCards(6);

        List<Card> allCards = new ArrayList<>(monthlyArtCards);
        allCards.addAll(yearEndPickCards);
        Set<Long> likedCardIds = getLikedCardIds(userId, allCards);

        CardResDTO.HomeSections.HomeSection monthlyArt = new CardResDTO.HomeSections.HomeSection(
                monthlyArtCards.stream().map(card -> cardConverter.toCardInfo(card, likedCardIds)).toList()
        );

        CardResDTO.HomeSections.HomeSection yearEndPick = new CardResDTO.HomeSections.HomeSection(
                yearEndPickCards.stream().map(card -> cardConverter.toCardInfo(card, likedCardIds)).toList()
        );

        return new CardResDTO.HomeSections(nickname, monthlyArt, yearEndPick);
    }
}