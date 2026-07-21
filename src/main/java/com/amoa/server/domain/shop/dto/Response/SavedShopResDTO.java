package com.amoa.server.domain.shop.dto.Response;

import com.amoa.server.domain.common.enums.ArtType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class SavedShopResDTO {

    public record LikeResultDTO(
            Long userShopId,
            Long shopId,
            LocalDateTime createdAt
    ) {
    }

    public record LikedShopListResponse(
            List<LikedShopResponse> likedShops,
            Long totalElements,
            Boolean hasNext
    ) {
    }

    public record LikedShopResponse(
            Long shopId,
            String shopName,
            LocalDateTime likedAt,
            String profileImageUrl,
            String regionName,
            List<CardPreviewDTO> cards
    ) {
    }

    public record CardPreviewDTO(
            Long cardId,
            String instagramUrl,
            ArtType artType,
            Integer maxPrice,
            Integer minPrice
    ) {
    }

}
