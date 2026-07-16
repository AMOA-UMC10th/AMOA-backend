package com.amoa.server.domain.shop.service.query;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.ShopDesignTagRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
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
    public ShopResDTO.ShopDetailResponse getShopDetail(Long shopId) {

        // 1) 샵 조회
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        // 2) 디자인태그 조회
        List<DesignTag> designTags = shopDesignTagRepository.findByShop_ShopId(shopId)
                .stream()
                .map(ShopDesignTag::getDesignTag)
                .toList();

        // 3) 찜 수 (임시로 0, 나중에 SavedShop/UserCard Entity 생기면 수정할 예정)
        int cardLikeCount = 0;
        int shopLikeCount = 0;
        boolean isLiked = false;

        return ShopConverter.toShopDetailResponse(shop, designTags, cardLikeCount, shopLikeCount, isLiked);
    }
}