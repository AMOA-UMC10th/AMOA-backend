package com.AMOA.server.domain.like.controller;

import com.AMOA.server.domain.like.service.ShopLikeService;
import com.AMOA.server.domain.shop.domain.Shop;
import com.AMOA.server.domain.shop.repository.ShopRepository;
import com.AMOA.server.domain.user.domain.User;
import com.AMOA.server.domain.user.repository.UserRepository;
import com.AMOA.server.global.apiPayload.ApiResponse;
import com.AMOA.server.global.apiPayload.code.GeneralSuccessCode;
import com.AMOA.server.global.apiPayload.code.ShopErrorCode;
import com.AMOA.server.global.apiPayload.code.UserErrorCode;
import com.AMOA.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shops")
public class ShopLikeController {

    private final ShopLikeService shopLikeService;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;


    @PostMapping("/{shopId}/likes")
    public ApiResponse<String> createShopLike(@PathVariable Long shopId) {

        User dummyUser = userRepository.findById(1L)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));
        Shop dummyShop = shopRepository.findById(shopId)
                .orElseThrow(() -> new GeneralException(ShopErrorCode.SHOP_NOT_FOUND));

        shopLikeService.createShopLike(dummyUser, dummyShop);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "네일숍 찜 등록에 성공했습니다.");

    }

    @DeleteMapping("/{shopId}/likes")
    public ApiResponse<String> deleteShopLike(@PathVariable Long shopId) {
        User dummyUser = userRepository.findById(1L)
                .orElseThrow(() -> new GeneralException(UserErrorCode.USER_NOT_FOUND));
        Shop dummyShop = shopRepository.findById(shopId)
                .orElseThrow(() -> new GeneralException(ShopErrorCode.SHOP_NOT_FOUND));

        shopLikeService.deleteShopLike(dummyUser, dummyShop);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "네일숍 찜 취소에 성공했습니다.");
    }
}
