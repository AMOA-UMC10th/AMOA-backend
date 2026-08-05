package com.amoa.server.domain.shop.dto.response;

import com.amoa.server.domain.common.dto.response.DesignTagResDTO;
import com.amoa.server.domain.shop.enums.ShopStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ShopResDTO {

    // GET /api/shops/search (카카오 로컬 API 검색)
    @Getter
    @Builder
    @AllArgsConstructor
    public static class KakaoSearchResponse {
        private String placeName;
        private String address;
        private String phone;
    }

    // GET /api/v1/shops/address/search
    @Builder
    public record ShopAddressSearchResponse(
            String roadAddress,
            String jibunAddress,
            BigDecimal latitude,
            BigDecimal longitude,
            String legalCode,
            String region1DepthName,
            String region2DepthName,
            String region3DepthName
    ) { }

    // POST /api/shops (샵 등록)
    public record CreateShopResponse(
            Long shopId,
            String shopName,
            String address,
            ShopStatus shopStatus,
            LocalDateTime createdAt
    ) {}

    // GET /api/shops/{shop_id}/cards (샵 상세 카드 목록 조회)
    public record CardListResponse(
            Long shopId,
            String shopName,
            long totalCount,
            List<CardResponse> cards,
            String nextCursor, // 커서 구조로 변경
            boolean hasNext
    ) {}

    // 카드 단건 응답
    public record CardResponse(
            Long cardId,
            String regionName,
            int minPrice,
            int maxPrice,
            String artType,
            boolean isLiked
    ) {}

    // GET /api/shops/{shop_id} (유저용 샵 상세 조회)
    public record ShopDetailResponse(
            Long shopId,
            String shopName,
            String address,
            String shopPhoneNumber,
            String businessHours,
            List<DesignTagResDTO.DesignTagResponse> designTags,
            int cardLikeCount,
            int shopLikeCount,
            boolean isLiked
    ) {}

}