package com.amoa.server.domain.shop.converter;

import com.amoa.server.domain.shop.dto.request.ShopOptionReqDTO.ShopOptionCreateReqDTO;
import com.amoa.server.domain.shop.dto.response.ShopOptionResDTO;
import com.amoa.server.domain.shop.dto.response.ShopOptionResDTO.OptionResult;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import java.util.List;

public class ShopOptionConverter {

    public static ShopOption toShopOption(
            Shop shop,
            ShopOptionCreateReqDTO request
    ) {
        return ShopOption.builder()
                .shop(shop)
                .optionName(request.optionName())
                .optionType(request.optionType())
                .optionPrice(request.optionPrice())
                .durationMinutes(request.durationMinutes())
                .maxQuantity(request.maxQuantity())
                .isActive(true)
                .build();
    }

    public static ShopOptionResDTO.OptionResult toOptionResult(
            ShopOption option
    ) {
        return new ShopOptionResDTO.OptionResult(
                option.getId(),
                option.getOptionName(),
                option.getOptionType(),
                option.getOptionPrice(),
                option.getDurationMinutes(),
                option.getMaxQuantity()
        );
    }

    public static ShopOptionResDTO.OptionListResult toOptionListResult(
            List<ShopOption> options
    ) {
        List<OptionResult> optionResults =
                options.stream()
                        .map(ShopOptionConverter::toOptionResult)
                        .toList();

        return new ShopOptionResDTO.OptionListResult(
                optionResults
        );
    }
}
