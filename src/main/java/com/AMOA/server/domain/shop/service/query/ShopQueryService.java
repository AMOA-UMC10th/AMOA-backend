package com.AMOA.server.domain.shop.service.query;

import com.AMOA.server.domain.common.entity.DesignTag;
import com.AMOA.server.domain.common.repository.DesignTagRepository;
import com.AMOA.server.domain.shop.converter.ShopConverter;
import com.AMOA.server.domain.shop.dto.Response.ShopResDTO;
import com.AMOA.server.domain.shop.exception.ShopException;
import com.AMOA.server.domain.shop.exception.code.ShopErrorCode;
import com.AMOA.server.global.kakao.KakaoLocalClient;
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
        List<DesignTag> designTags = designTagRepository.findAll();
        return ShopConverter.toDesignTagListResponse(designTags);
    }

    // GET /api/admin/shops/search - 카카오 로컬 API 검색
    public ShopResDTO.KakaoSearchResponse searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ShopException(ShopErrorCode.SHOP_KEYWORD_EMPTY);
        }
        ShopResDTO.KakaoSearchResponse result = kakaoLocalClient.searchByKeyword(keyword);
        if (result == null) {
            throw new ShopException(ShopErrorCode.KAKAO_API_ERROR);
        }
        return result;
    }
}