package com.amoa.server.domain.card.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class UserCardResDTO {

    public record LikeResultDTO(
            Long userCardId,
            Long cardId,
            LocalDateTime createdAt
    ) {}

    public record LikedCardResponse(
            Long cardId,
            String instagramUrl,
            String artType,
            String shopName,
            String district,
            Integer minPrice,
            Integer maxPrice,
            LocalDateTime likedAt
    ) {}

    public record LikedCardListResponse(
            List<LikedCardResponse> cards,
            long totalCount,
            boolean hasNext
    ){}
}