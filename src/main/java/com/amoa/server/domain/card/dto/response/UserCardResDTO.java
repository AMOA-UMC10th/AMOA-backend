package com.amoa.server.domain.card.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

public class UserCardResDTO {

    @Builder
    public record LikeResultDTO(
            Long userCardId,
            Long cardId,
            LocalDateTime createdAt
    ) {
    }
}