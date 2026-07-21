package com.amoa.server.domain.shop.controller;

import com.amoa.server.domain.shop.controller.docs.SavedShopControllerDocs;
import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.domain.shop.exception.code.ShopSuccessCode;
import com.amoa.server.domain.shop.service.command.SavedShopCommandService;
import com.amoa.server.domain.shop.service.query.SavedShopQueryService;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.amoa.server.global.auth.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shops")
public class SavedShopController implements SavedShopControllerDocs{

    private final SavedShopCommandService savedShopCommandService;
    private final SavedShopQueryService savedShopQueryService;

    @Override
    @PostMapping("/{shopId}/like")
    public ApiResponse<SavedShopResDTO.LikeResultDTO> createShopLike(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        User user = customUserDetails.user();

        SavedShopResDTO.LikeResultDTO result = savedShopCommandService.createShopLike(user, shopId);

        return ApiResponse.onSuccess(ShopSuccessCode.SHOP_LIKED, result);

    }

    @Override
    @DeleteMapping("/{shopId}/like")
    public ApiResponse<Void> deleteShopLike(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {

        User user = customUserDetails.user();

        savedShopCommandService.deleteShopLike(user, shopId);

        return ApiResponse.onSuccess(ShopSuccessCode.SHOP_UNLIKED,null);
    }

}
