package com.amoa.server.domain.shop.dto.Response;

import com.amoa.server.domain.common.enums.ArtType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class SavedShopResDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResultDTO{
        private Long userShopId;
        private Long shopId;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikedShopListResponse {

        private List<LikedShopResponse> likedShops;
        private Long totalElements;
        private Boolean hasNext;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikedShopResponse {

        private Long shopId;
        private String shopName;
        private LocalDateTime likedAt;
        private String profileImageUrl;
        private String regionName;
        private List<CardPreviewDTO> cards;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardPreviewDTO {

        private Long cardId;
        private String instagramUrl;
        private ArtType artType;
        private Integer maxPrice;
        private Integer minPrice;
    }

}
