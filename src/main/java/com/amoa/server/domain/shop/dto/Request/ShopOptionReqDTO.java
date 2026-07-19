package com.amoa.server.domain.shop.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ShopOptionReqDTO {

    public record ShopOptionCreateReqDTO(

            @NotBlank
            String optionName,

            @Positive
            int optionPrice,

            @Positive
            int durationMinutes,

            @Positive
            int maxQuantity
    ) {
    }
}
