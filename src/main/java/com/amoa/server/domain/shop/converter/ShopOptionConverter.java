package com.amoa.server.domain.shop.converter;

import com.amoa.server.domain.shop.dto.Request.ShopOptionReqDTO.ShopOptionCreateReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopOptionResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;

public class ShopOptionConverter {

    public static ShopOption toShopOption(
            Shop shop,
            ShopOptionCreateReqDTO request
    ) {
        return ShopOption.builder()
                .shop(shop)
                .optionName(request.optionName())
                .optionPrice(request.optionPrice())
                .durationMinutes(request.durationMinutes())
                .maxQuantity(request.maxQuantity())
                .isActive(true)
                .build();
    }

    public static ShopOptionResDTO.CreateResult toCreateResult(
            ShopOption option
    ) {
        return new ShopOptionResDTO.CreateResult(
                option.getId(),
                option.getOptionName(),
                option.getOptionPrice(),
                option.getDurationMinutes(),
                option.getMaxQuantity()
        );
    }
}
