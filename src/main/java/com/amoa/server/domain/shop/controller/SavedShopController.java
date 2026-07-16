package com.amoa.server.domain.shop.controller;

import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.exception.code.ShopSuccessCode;
import com.amoa.server.domain.shop.repository.ShopRepository;
import com.amoa.server.domain.shop.service.command.SavedShopService;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shops")
public class SavedShopController {

    private final SavedShopService savedShopService;

    @PostMapping("/{shopId}/like")
    public ApiResponse<SavedShopResDTO.LikeResultDTO> createShopLike(
            @PathVariable Long shopId,
            @AuthenticationPrincipal User user
    ) {

        SavedShopResDTO.LikeResultDTO result = savedShopService.createShopLike(user, shopId);

        return ApiResponse.onSuccess(ShopSuccessCode.SHOP_LIKED, result);

    }

    @DeleteMapping("/{shopId}/like")
    public ApiResponse<Void> deleteShopLike(
            @PathVariable Long shopId,
            @AuthenticationPrincipal User user
    ) {

        savedShopService.deleteShopLike(user, shopId);

        return ApiResponse.onSuccess(ShopSuccessCode.SHOP_UNLIKED,null);
    }
}
