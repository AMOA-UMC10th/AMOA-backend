package com.amoa.server.domain.shop.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ShopOptionReqDTO {

    public record ShopOptionCreateReqDTO(

            @NotBlank
            @Size(max = 50)
            String optionName,

            @NotNull
            @PositiveOrZero
            Integer optionPrice,

            @NotNull
            @PositiveOrZero
            Integer durationMinutes,

            @NotNull
            @Positive
            Integer maxQuantity
    ) {}

    public record UpdateRequest(

            @NotBlank
            @Size(max = 50)
            String optionName,

            @PositiveOrZero
            Integer optionPrice,

            @PositiveOrZero
            Integer durationMinutes,

            @Positive
            Integer maxQuantity
    ) {}
}
