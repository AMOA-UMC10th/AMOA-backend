package com.amoa.server.domain.shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ShopReqDTO {

    public record CreateShopRequest(
            @NotBlank(message = "샵 이름은 필수입니다.")
            String shopName,

            @NotBlank(message = "카카오톡 채널 URL은 필수입니다.")
            String kakaoChannelUrl,

            String instagramUrl,
            String profileImageUrl,

            @NotBlank(message = "주소는 필수입니다.")
            String address,

            String shopPhoneNumber,
            String businessHours,

            @NotNull
            @Min(value = 0, message = "예약금은 0원 이상이어야 합니다.")
            Integer depositAmount,

            List<Long> designtagIds
    ) {}
}