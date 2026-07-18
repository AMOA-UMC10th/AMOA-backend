package com.amoa.server.domain.shop.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

}
