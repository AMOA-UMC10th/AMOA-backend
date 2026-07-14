package com.amoa.server.domain.shop.service.query;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
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
}