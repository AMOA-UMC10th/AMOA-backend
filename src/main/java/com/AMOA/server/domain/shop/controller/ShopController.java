package com.amoa.server.domain.shop.controller;

import com.amoa.server.domain.shop.controller.docs.ShopControllerDocs;
import com.amoa.server.domain.shop.dto.Request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO.CreateShopResponse;
import com.amoa.server.domain.shop.exception.code.ShopSuccessCode;
import com.amoa.server.domain.shop.service.command.ShopCommandService;
import com.amoa.server.domain.shop.service.query.ShopQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shops")
@RequiredArgsConstructor
public class ShopController implements ShopControllerDocs {

    private final ShopCommandService shopCommandService;
    private final ShopQueryService shopQueryService;

    // GET /api/admin/shops/designtag - 디자인태그 목록 조회
    @GetMapping("/designtag")
    public ApiResponse<ShopResDTO.DesignTagListResponse> getDesignTags() {
        ShopResDTO.DesignTagListResponse result = shopQueryService.getDesignTags();
        return ApiResponse.onSuccess(ShopSuccessCode.DESIGNTAG_LIST_FOUND, result);
    }

    // GET /api/admin/shops/search - 샵 이름으로 카카오 로컬 API 검색
    @GetMapping("/search")
    public ApiResponse<ShopResDTO.KakaoSearchResponse> searchShopByKeyword(
            @RequestParam String keyword) {
        ShopResDTO.KakaoSearchResponse result = shopQueryService.searchByKeyword(keyword);
        return ApiResponse.onSuccess(ShopSuccessCode.KAKAO_SEARCH_FOUND, result);
    }

    // POST /api/admin/shops - 샵 등록
    @PostMapping
    public ResponseEntity<ApiResponse<CreateShopResponse>> createShop(
            @RequestBody @Valid ShopReqDTO.CreateShopRequest request) {
        ShopResDTO.CreateShopResponse result = shopCommandService.createShop(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(ShopSuccessCode.SHOP_CREATED, result));
    }
}