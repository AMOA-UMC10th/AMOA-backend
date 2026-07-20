package com.amoa.server.domain.shop.controller;

import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.shop.controller.docs.ShopControllerDocs;
import com.amoa.server.domain.shop.dto.Request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO.CreateShopResponse;
import com.amoa.server.domain.shop.exception.code.ShopSuccessCode;
import com.amoa.server.domain.shop.service.command.ShopCommandService;
import com.amoa.server.domain.shop.service.query.ShopQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ShopController implements ShopControllerDocs {

    private final ShopCommandService shopCommandService;
    private final ShopQueryService shopQueryService;

    // GET /api/admin/shops/designtag - 디자인태그 목록 조회
    @GetMapping("/api/admin/shops/designtag")
    public ApiResponse<ShopResDTO.DesignTagListResponse> getDesignTags() {
        ShopResDTO.DesignTagListResponse result = shopQueryService.getDesignTags();
        return ApiResponse.onSuccess(ShopSuccessCode.DESIGN_TAG_LIST_FOUND, result);
    }

    // GET /api/admin/shops/search - 샵 이름으로 카카오 로컬 API 검색
    @GetMapping("/api/admin/shops/search")
    public ApiResponse<ShopResDTO.KakaoSearchResponse> searchShopByKeyword(
            @RequestParam String keyword) {
        ShopResDTO.KakaoSearchResponse result = shopQueryService.searchByKeyword(keyword);
        return ApiResponse.onSuccess(ShopSuccessCode.KAKAO_SEARCH_FOUND, result);
    }

    // POST /api/admin/shops - 샵 등록
    @PostMapping("/api/admin/shops")
    public ResponseEntity<ApiResponse<ShopResDTO.CreateShopResponse>> createShop(
            @RequestBody @Valid ShopReqDTO.CreateShopRequest request) {
        ShopResDTO.CreateShopResponse result = shopCommandService.createShop(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(ShopSuccessCode.SHOP_CREATED, result));
    }

    // GET /api/shops/{shopId}/cards - 샵 상세 카드 목록 조회
    @GetMapping("/api/shops/{shopId}/cards")
    public ApiResponse<ShopResDTO.CardListResponse> getShopCards(
            @PathVariable Long shopId,
            @RequestParam(required = false) ArtType artType,
            @RequestParam(defaultValue = "LATEST") SortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // Long userId = customUserDetails.user().getId();
        Long userId = customUserDetails != null ? customUserDetails.user().getId() : null; // 인증 에러 문제 나는 동안만 사용
        ShopResDTO.CardListResponse result = shopQueryService.getShopCards(shopId, artType, sort, page, size, userId);
        return ApiResponse.onSuccess(ShopSuccessCode.CARD_LIST_FOUND, result);
    }

    // GET /api/shops/{shopId} - 샵 상세 조회 (유저)
    @GetMapping("/api/shops/{shopId}")
    public ApiResponse<ShopResDTO.ShopDetailResponse> getShopDetail(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // Long userId = customUserDetails.user().getId();
        Long userId = customUserDetails != null ? customUserDetails.user().getId() : null;
        ShopResDTO.ShopDetailResponse result = shopQueryService.getShopDetail(shopId, userId);
        return ApiResponse.onSuccess(ShopSuccessCode.SHOP_FOUND, result);
    }
}