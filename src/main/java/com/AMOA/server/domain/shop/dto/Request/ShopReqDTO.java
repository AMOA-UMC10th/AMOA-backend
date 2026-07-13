package com.AMOA.server.domain.shop.dto.Request;

import java.util.List;

public class ShopReqDTO {

    // POST /api/admin/shops (샵 등록)
    public record CreateShopRequest(
            String shopName,
            String kakaoChannelUrl,
            String instagramUrl,
            String profileImageUrl,
            String address,
            String shopPhoneNumber,
            String businessHours,
            List<Long> designtagIds
    ) {}
}