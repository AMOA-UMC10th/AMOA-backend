package com.AMOA.server.domain.shop.dto.Request;

import jakarta.validation.constraints.NotBlank;
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

            List<Long> designtagIds
    ) {}
}