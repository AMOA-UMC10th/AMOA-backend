package com.amoa.server.domain.shop.service.query;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.card.repository.UserCardRepository;
import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import com.amoa.server.domain.shop.repository.SavedShopRepository;
import com.amoa.server.domain.shop.repository.ShopDesignTagRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.kakao.KakaoLocalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // GET /api/shops/{shop_id}/cards - 샵 상세 카드 목록 조회
    public ShopResDTO.CardListResponse getShopCards(
            Long shopId,
            ArtType artType,
            SortType sort,
            int page,
            int size,
            Long userId) {

        // 1) 샵 조회
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        // 2) 정렬 설정
        SortType activeSort = sort != null ? sort : SortType.LATEST;
        Sort sorting = switch (activeSort) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR -> Sort.by(Sort.Direction.DESC, "likeCard");
            case PRICE_ASC -> Sort.by(Sort.Direction.ASC, "minPrice");
            case PRICE_DESC -> Sort.by(Sort.Direction.DESC, "maxPrice");
            case RECOMMENDED -> Sort.by(Sort.Direction.DESC, "likeCard");
            // 추천순은 임시로 찜 개수를 기준으로 만들어둠 -> 온보딩에서 유저의 관심 지역, 관심 디자인태그 먼저 구현된 후 추천순 로직 구현
        };

        Pageable pageable = PageRequest.of(page, size, sorting);

        // 3) 카드 목록 조회
        Page<Card> cards;
        if (artType == null) {
            cards = cardRepository.findByShop_IdAndDeletedAtIsNull(shopId, pageable);
        } else {
            cards = cardRepository.findByShop_IdAndArtTypeAndDeletedAtIsNull(shopId, artType, pageable);
        }

        // 4) 찜 여부
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        List<ShopResDTO.CardResponse> cardResponses = cards.getContent().stream()
                .map(card -> {
                    boolean isLiked = user != null && userCardRepository.existsByUserAndCard(user, card);
                    return ShopConverter.toCardResponse(card, isLiked);
                })
                .toList();

        return ShopConverter.toCardListResponse(shop, cards, cardResponses);

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
        User user = userRepository.findById(userId).orElseThrow(() -> new ShopException(ShopErrorCode.USER_NOT_FOUND));

        boolean isLiked = savedShopRepository.existsByUserAndShop(user, shop);

        return ShopConverter.toShopDetailResponse(shop, designTags, cardLikeCount, shopLikeCount, isLiked);
    }
}