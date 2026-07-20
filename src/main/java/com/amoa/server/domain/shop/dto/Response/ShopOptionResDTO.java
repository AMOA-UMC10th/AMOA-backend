package com.amoa.server.domain.shop.dto.Response;

import java.util.List;

public class ShopOptionResDTO {

    public record OptionResult(
            Long optionId,
            String optionName,
            int optionPrice,
            int durationMinutes,
            int maxQuantity
    ) {}

    public record OptionListResult(
            List<OptionResult> options
    ) {
    }
}
