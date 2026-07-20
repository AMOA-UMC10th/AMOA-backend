package com.amoa.server.domain.shop.dto.Response;

public class ShopOptionResDTO {

    public record CreateResult(
            Long optionId,
            String optionName,
            int optionPrice,
            int durationMinutes,
            int maxQuantity
    ) {
    }
}
