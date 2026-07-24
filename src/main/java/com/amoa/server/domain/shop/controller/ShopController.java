package com.amoa.server.domain.shop.controller;

import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.shop.controller.docs.ShopControllerDocs;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.exception.code.ShopSuccessCode;
import com.amoa.server.domain.shop.service.query.ShopQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class ShopController implements ShopControllerDocs {

    private final ShopQueryService shopQueryService;

    // GET /api/shops/{shopId}/cards - 샵 상세 카드 목록 조회
    @GetMapping("/{shopId}/cards")
    public ApiResponse<ShopResDTO.CardListResponse> getShopCards(
            @PathVariable Long shopId,
            @RequestParam(required = false) ArtType artType,
            @RequestParam(defaultValue = "LATEST") SortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails != null ? customUserDetails.user().getId() : null;
        ShopResDTO.CardListResponse result = shopQueryService.getShopCards(shopId, artType, sort, page, size, userId);
        return ApiResponse.onSuccess(ShopSuccessCode.CARD_LIST_FOUND, result);
    }

    // GET /api/shops/{shopId} - 샵 상세 조회 (유저)
    @GetMapping("/{shopId}")
    public ApiResponse<ShopResDTO.ShopDetailResponse> getShopDetail(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails != null ? customUserDetails.user().getId() : null;
        ShopResDTO.ShopDetailResponse result = shopQueryService.getShopDetail(shopId, userId);
        return ApiResponse.onSuccess(ShopSuccessCode.SHOP_FOUND, result);
    }
}