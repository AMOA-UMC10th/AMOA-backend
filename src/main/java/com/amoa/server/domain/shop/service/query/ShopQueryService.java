package com.amoa.server.domain.shop.service.query;

import com.amoa.server.domain.card.dto.request.CardReqDTO;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.repository.CardDesignTagRepository;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.card.repository.UserCardRepository;
import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.response.ShopResDTO;
import com.amoa.server.domain.shop.dto.response.ShopResDTO.ShopAddressSearchResponse;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import com.amoa.server.domain.shop.repository.SavedShopRepository;
import com.amoa.server.domain.shop.repository.ShopDesignTagRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.repository.UserDesignTagRepository;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.kakao.KakaoLocalClient;
import com.amoa.server.global.kakao.dto.response.KakaoAddressResDTO;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopQueryService {

    private final DesignTagRepository designTagRepository;
    private final KakaoLocalClient kakaoLocalClient;
    private final CardRepository cardRepository;
    private final ShopRepository shopRepository;
    private final UserCardRepository userCardRepository;
    private final UserRepository userRepository;
    private final ShopDesignTagRepository shopDesignTagRepository;
    private final SavedShopRepository savedShopRepository;
    private final CardDesignTagRepository cardDesignTagRepository;
    private final UserDesignTagRepository userDesignTagRepository;

    // GET /api/admin/shops/designtag - 디자인태그 목록 조회
    public ShopResDTO.DesignTagListResponse getDesignTags() {
        List<DesignTag> designTags = designTagRepository.findAllByOrderByIdAsc();
        return ShopConverter.toDesignTagListResponse(designTags);
    }

    // GET /api/admin/shops/search - 카카오 로컬 API 검색
    public ShopResDTO.KakaoSearchResponse searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ShopException(ShopErrorCode.SHOP_KEYWORD_EMPTY);
        }

        ShopResDTO.KakaoSearchResponse result = kakaoLocalClient.searchByKeyword(keyword);

        if (result == null) {
            // API 오류가 아니라 검색 결과 없음
            throw new ShopException(ShopErrorCode.KAKAO_SEARCH_NOT_FOUND);
        }

        return result;
    }

    // GET /api/admin/shops/address/search - 주소 검색
    public ShopAddressSearchResponse searchAddress(String address) {

        if (address == null || address.isBlank()) {
            throw new ShopException(ShopErrorCode.SHOP_INVALID_ADDRESS);
        }

        KakaoAddressResDTO.AddressResponse response =
                kakaoLocalClient.searchAddress(address);

        KakaoAddressResDTO.Document document =
                response.documents().get(0);

        return ShopConverter.toAddressSearchResponse(document);
    }

    // GET /api/shops/{shop_id}/cards - 샵 상세 카드 목록 조회 (무한 스크롤)
    public ShopResDTO.CardListResponse getShopCards(
            Long shopId,
            ArtType artType,
            SortType sort,
            String cursor,
            int size,
            Long userId
    ) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        SortType activeSort = sort != null ? sort : SortType.RECOMMENDED;

        List<Long> preferredDesignTagIds = (activeSort == SortType.RECOMMENDED && userId != null)
                ? userDesignTagRepository.findAllByUser_Id(userId).stream()
                .map(userDesignTag -> userDesignTag.getDesignTag().getId())
                .toList()
                : List.of();

        CardReqDTO.ShopCardSearchRequest request = new CardReqDTO.ShopCardSearchRequest(
                shopId, artType, activeSort, cursor, preferredDesignTagIds
        );

        List<Card> cards = cardRepository.findShopCards(request, size);
        Long totalCount = cardRepository.countShopCards(shopId, artType);

        boolean hasNext = cards.size() > size;
        if (hasNext) {
            cards = cards.subList(0, size);
        }

        Set<Long> likedCardIds = getLikedCardIds(userId, cards);
        List<ShopResDTO.CardResponse> cardResponses = cards.stream()
                .map(card -> ShopConverter.toCardResponse(card, likedCardIds.contains(card.getId())))
                .toList();

        String nextCursor = hasNext
                ? buildCursor(cards.get(cards.size() - 1), activeSort, preferredDesignTagIds)
                : null;

        return ShopConverter.toCardListResponse(shop, totalCount, cardResponses, nextCursor, hasNext);
    }

    private static final String MOOD_CURSOR_PREFIX = "M:";
    private static final String STANDARD_CURSOR_PREFIX = "S:";

    private String buildCursor(Card card, SortType sort, List<Long> preferredDesignTagIds) {
        if (sort == SortType.RECOMMENDED && !preferredDesignTagIds.isEmpty()) {
            boolean matches = cardDesignTagRepository
                    .existsByCard_IdAndDesignTag_IdIn(card.getId(), preferredDesignTagIds);
            int priority = matches ? 0 : 1;
            return MOOD_CURSOR_PREFIX + priority + "_" + card.getLikeCard() + "_" + card.getId();
        }

        String body = switch (sort) {
            case PRICE_ASC, PRICE_DESC -> card.getMinPrice() + "_" + card.getMaxPrice() + "_" + card.getId();
            case POPULAR, RECOMMENDED -> card.getLikeCard() + "_" + card.getId();
            case LATEST -> card.getCreatedAt() + "_" + card.getId();
        };
        return STANDARD_CURSOR_PREFIX + body;
    }

    // GET /api/shops/{shop_id} - 샵 상세 조회 (유저)
    public ShopResDTO.ShopDetailResponse getShopDetail (Long shopId, Long userId){

        // 1) 샵 조회
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        // 2) 디자인태그 조회
        List<DesignTag> designTags = shopDesignTagRepository.findByShop_Id(shopId)
                .stream()
                .map(ShopDesignTag::getDesignTag)
                .toList();

        // 3) 샵 찜 수
        int shopLikeCount = savedShopRepository.countByShop(shop);

        // 4) 아트 찜 수
        int cardLikeCount = cardRepository.countCardLikesByShopId(shopId);

        // 5) 현재 유저의 샵 찜 여부
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        boolean isLiked = savedShopRepository.existsByUserAndShop(user, shop);

        return ShopConverter.toShopDetailResponse(shop, designTags, cardLikeCount, shopLikeCount, isLiked);
    }

    private Set<Long> getLikedCardIds(Long userId, List<Card> cards) {
        if (userId == null || cards.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> cardIds = cards.stream().map(Card::getId).toList();
        return userCardRepository.findByUserIdAndCardIdIn(userId, cardIds)
                .stream()
                .map(userCard -> userCard.getCard().getId())
                .collect(Collectors.toSet());
    }
}