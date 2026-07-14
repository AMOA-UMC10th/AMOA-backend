package com.AMOA.server.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ShopLikeResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResultDTO{
        private Long shopLikeId;
        private LocalDateTime createdAt;
    }

}
