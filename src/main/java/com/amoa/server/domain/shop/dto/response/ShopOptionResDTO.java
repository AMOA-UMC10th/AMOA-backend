package com.amoa.server.domain.shop.dto.response;

import com.amoa.server.domain.shop.enums.ShopOptionType;
import java.util.List;

public class ShopOptionResDTO {

    public record OptionResult(
            Long optionId,
            String optionName,
            ShopOptionType optionType,
            int optionPrice,
            int durationMinutes,
            int maxQuantity
    ) {}

    public record OptionListResult(
            List<OptionResult> options
    ) {
    }
}
