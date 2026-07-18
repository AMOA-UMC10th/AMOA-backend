package com.amoa.server.domain.shop.dto.Response;

import com.amoa.server.domain.shop.enums.ShopStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ShopResDTO {

    // GET /api/admin/shops/designtag (디자인태그 목록 조회)
    public record DesignTagResponse(
            Long designtagId,
            String name
    ) {}

    // 디자인태그 목록
    public record DesignTagListResponse(
            List<DesignTagResponse> designtags
    ) {}

    // GET /api/admin/shops/search (카카오 로컬 API 검색)
    @Getter
    @Builder
    @AllArgsConstructor
    public static class KakaoSearchResponse {
        private String placeName;
        private String address;
        private String phone;
    }

    // POST /api/admin/shops (샵 등록)
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
            int totalCount,
            int page,
            int size,
            List<CardResponse> cards
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
}