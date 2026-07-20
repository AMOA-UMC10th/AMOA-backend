package com.amoa.server.domain.card.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserCardResDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResultDTO {
        private Long userCardId;
        private Long cardId;
        private LocalDateTime createdAt;
    }
}
