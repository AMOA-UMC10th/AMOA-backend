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
        // 온보딩 정보 조회
        List<Long> onboardingRegionIds = getOnboardingRegionIds(userId);
        List<Long> onboardingDesignTagIds = getOnboardingDesignTagIds(userId);

        // 온보딩이 없으면 일반 조회
        boolean hasOnboardingRegion = !onboardingRegionIds.isEmpty();
        boolean hasOnboardingDesignTag = !onboardingDesignTagIds.isEmpty();

        if (!hasOnboardingRegion && !hasOnboardingDesignTag) {
            // 온보딩 정보 없음 → 추천순 = 인기순
            return searchCardsDefault(request, userId);
        }

        // 검색 조건 여부
        boolean hasRegionSearch = request.regionIds() != null && !request.regionIds().isEmpty();
        boolean hasDesignTagSearch = request.designTagIds() != null && !request.designTagIds().isEmpty();

        int size = request.size() == null ? 20 : Math.max(1, Math.min(request.size(), 100));

        // Case 1: 검색 조건이 완전함 (지역O + 무드O)
        if (hasRegionSearch && hasDesignTagSearch) {
            return searchCardsDefault(request, userId);
        }

        // Case 2: 지역만 검색 (무드는 온보딩 적용)
        if (hasRegionSearch && !hasDesignTagSearch) {
            // 온보딩 무드가 없으면 일반 조회
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
                    request.cursor(),
                    request.size()
            );

            return handleTwoStageSearch(stage1Request, request, userId, size, true, false,
                    onboardingRegionIds, onboardingDesignTagIds);
        }

        // Case 3: 무드만 검색 (지역은 온보딩 적용)
        if (!hasRegionSearch && hasDesignTagSearch) {
            // 온보딩 지역이 없으면 일반 조회
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
                    request.cursor(),
                    request.size()
            );

            return handleTwoStageSearch(stage1Request, request, userId, size, false, true,
                    onboardingRegionIds, onboardingDesignTagIds);
        }

        // Case 4: 검색 조건 없음 (둘 다 온보딩 적용)
        CardSearchRequest stage1Request = new CardSearchRequest(
                onboardingRegionIds.isEmpty() ? null : onboardingRegionIds,
                request.minPrice(),
                request.maxPrice(),
                request.artType(),
                onboardingDesignTagIds.isEmpty() ? null : onboardingDesignTagIds,
                SortType.RECOMMENDED,
                request.cursor(),
                request.size()
        );

        return handleTwoStageSearch(stage1Request, request, userId, size, false, false,
                onboardingRegionIds, onboardingDesignTagIds);
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
            List<Long> onboardingDesignTagIds
    ) {
        // 1단계 조회
        List<Card> stage1Cards = cardRepository.findCards(stage1Request, size);
        Long stage1TotalCount = cardRepository.countCards(stage1Request);

        // 1단계에서 충분한 카드가 있으면 반환
        if (stage1Cards.size() > size) {
            List<Card> finalCards = stage1Cards.subList(0, size);
            Set<Long> likedCardIds = getLikedCardIds(userId, finalCards);
            List<CardResDTO.CardInfo> cardInfos = finalCards.stream()
                    .map(card -> cardConverter.toCardInfo(card, likedCardIds))
                    .toList();

            String nextCursor = createCursor(finalCards.get(finalCards.size() - 1), SortType.RECOMMENDED);

            return new CardResDTO.CardList(
                    stage1TotalCount,  // 1단계 조건의 전체 개수 (정확함)
                    cardInfos.size(),
                    cardInfos,
                    nextCursor,
                    true
            );
        }

        // 1단계 카드가 부족하면 2단계 조회
        int remainingSize = size - stage1Cards.size();

        CardSearchRequest stage2Request;

        if (isRegionSearchOnly) {
            // 무드 검색 없이 (지역, 가격, 아트타입은 있을 수 있음)
            // 2단계: 검색 지역 + 가격/아트타입 유지 (온보딩 무드 제외)
            stage2Request = new CardSearchRequest(
                    originalRequest.regionIds(),
                    originalRequest.minPrice(),
                    originalRequest.maxPrice(),
                    originalRequest.artType(),
                    null,
                    SortType.RECOMMENDED,
                    null,
                    remainingSize + size
            );
        } else if (isDesignTagSearchOnly) {
            // 지역 검색 없이 (무드, 가격, 아트타입은 있을 수 있음)
            // 2단계: 검색 무드 + 가격/아트타입 유지 (온보딩 지역 제외)
            stage2Request = new CardSearchRequest(
                    null,
                    originalRequest.minPrice(),
                    originalRequest.maxPrice(),
                    originalRequest.artType(),
                    originalRequest.designTagIds(),
                    SortType.RECOMMENDED,
                    null,
                    remainingSize + size
            );
        } else {
            // 지역, 무드 모두 검색 없음 (가격, 아트타입은 있을 수 있음)
            // 2단계: 전체 + 가격/아트타입 (온보딩 조건 제외)
            stage2Request = new CardSearchRequest(
                    null,
                    originalRequest.minPrice(),
                    originalRequest.maxPrice(),
                    originalRequest.artType(),
                    null,
                    SortType.RECOMMENDED,
                    null,
                    remainingSize + size
            );
        }

        List<Card> stage2Cards = cardRepository.findCards(stage2Request, remainingSize + size);

        // 1단계에서 조회한 카드 ID 추출
        Set<Long> stage1CardIds = stage1Cards.stream()
                .map(Card::getId)
                .collect(Collectors.toSet());

        // 2단계에서 정확한 개수 조회 (1단계 카드 제외)
        Long stage2TotalCount = cardRepository.countCardsExcludingIds(stage2Request, stage1CardIds);

        // 2단계에서 1단계 카드 제외
        List<Card> filteredStage2Cards = stage2Cards.stream()
                .filter(card -> !stage1CardIds.contains(card.getId()))
                .limit(remainingSize)
                .collect(Collectors.toList());

        // 1단계 + 2단계 합치기
        List<Card> allCards = new ArrayList<>(stage1Cards);
        allCards.addAll(filteredStage2Cards);

        Set<Long> likedCardIds = getLikedCardIds(userId, allCards);
        List<CardResDTO.CardInfo> cardInfos = allCards.stream()
                .map(card -> cardConverter.toCardInfo(card, likedCardIds))
                .toList();

        String nextCursor = null;
        boolean hasNext = false;

        if (filteredStage2Cards.size() >= remainingSize) {
            // 2단계에서 더 있거나 정확히 남은 개수만큼 찼을 때
            hasNext = true;
            nextCursor = createCursor(filteredStage2Cards.get(remainingSize - 1), SortType.RECOMMENDED);
        }

        // totalCount: 1단계 + 2단계 (필터링 후)
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
}