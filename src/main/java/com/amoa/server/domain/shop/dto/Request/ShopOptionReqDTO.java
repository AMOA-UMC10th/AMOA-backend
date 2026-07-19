package com.amoa.server.domain.shop.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ShopOptionReqDTO {

    public record ShopOptionCreateReqDTO(

            @NotBlank
            @Size(max = 50)
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
