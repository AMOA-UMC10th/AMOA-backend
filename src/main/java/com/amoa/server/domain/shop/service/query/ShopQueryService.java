package com.amoa.server.domain.shop.service.query;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.SavedShopRepository;
import com.amoa.server.domain.shop.repository.ShopDesignTagRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.kakao.KakaoLocalClient;
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
    private final ShopRepository shopRepository;
    private final ShopDesignTagRepository shopDesignTagRepository;
    private final SavedShopRepository savedShopRepository;
    private final UserRepository userRepository;

    // GET /api/admin/shops/designtag - 디자인태그 목록 조회
    public ShopResDTO.DesignTagListResponse getDesignTags() {
        List<DesignTag> designTags = designTagRepository.findAllByOrderByDesignTagIdAsc();
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

    // GET /api/shops/{shop_id} - 샵 상세 조회 (유저)
    public ShopResDTO.ShopDetailResponse getShopDetail(Long shopId, Long userId) {

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

        // 4) 아트 찜 수 (일단 0, 나중에 추가)
        int cardLikeCount = 0;

        // 5) 현재 유저의 샵 찜 여부
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.USER_NOT_FOUND));
        boolean isLiked = savedShopRepository.existsByUserAndShop(user, shop);

        return ShopConverter.toShopDetailResponse(shop, designTags, cardLikeCount, shopLikeCount, isLiked);
    }
}