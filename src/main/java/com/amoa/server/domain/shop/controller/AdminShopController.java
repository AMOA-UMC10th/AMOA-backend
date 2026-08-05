package com.amoa.server.domain.shop.controller;

import com.amoa.server.domain.shop.controller.docs.AdminShopControllerDocs;
import com.amoa.server.domain.shop.dto.request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.response.ShopResDTO;
import com.amoa.server.domain.shop.dto.response.ShopResDTO.ShopAddressSearchResponse;
import com.amoa.server.domain.shop.exception.code.ShopSuccessCode;
import com.amoa.server.domain.shop.service.command.ShopCommandService;
import com.amoa.server.domain.shop.service.query.ShopQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class AdminShopController implements AdminShopControllerDocs {

    private final ShopCommandService shopCommandService;
    private final ShopQueryService shopQueryService;

    // GET /api/v1/shops/search - 샵 이름으로 카카오 로컬 API 검색
    @GetMapping("/search")
    public ApiResponse<ShopResDTO.KakaoSearchResponse> searchShopByKeyword(
            @RequestParam String keyword) {
        ShopResDTO.KakaoSearchResponse result = shopQueryService.searchByKeyword(keyword);
        return ApiResponse.onSuccess(ShopSuccessCode.KAKAO_SEARCH_FOUND, result);
    }

    // GET /api/v1/shops/address/search - 주소검색으로 카카오 로컬 API 검색
    @Override
    @GetMapping("/addresses")
    public ApiResponse<ShopAddressSearchResponse> searchAddress(
            @RequestParam String address
    ) {
        return ApiResponse.onSuccess(
                ShopSuccessCode.SHOP_ADDRESS_SEARCH_OK,
                shopQueryService.searchAddress(address)
        );

    }

    // POST /api/v1/shops - 샵 등록
    @PostMapping()
    public ResponseEntity<ApiResponse<ShopResDTO.CreateShopResponse>> createShop(
            @RequestBody @Valid ShopReqDTO.CreateShopRequest request) {
        ShopResDTO.CreateShopResponse result = shopCommandService.createShop(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(ShopSuccessCode.SHOP_CREATED, result));
    }

}