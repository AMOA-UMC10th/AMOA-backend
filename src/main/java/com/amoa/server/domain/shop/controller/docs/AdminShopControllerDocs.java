package com.amoa.server.domain.shop.controller.docs;

import com.amoa.server.domain.shop.dto.request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.response.ShopResDTO;
import com.amoa.server.domain.shop.dto.response.ShopResDTO.CreateShopResponse;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Shop(management)", description = "샵 관련 API(management)")
public interface AdminShopControllerDocs {

    @Operation(
            summary = "샵 이름으로 카카오 로컬 API 검색",
            description = "샵 이름으로 카카오 로컬 API를 검색하여 주소, 전화번호를 자동완성합니다."
    )
    ApiResponse<ShopResDTO.KakaoSearchResponse> searchShopByKeyword(
            @RequestParam String keyword
    );

    @Operation(
            summary = "주소로 샵 주소 검색 API",
            description = "도로명 또는 지번 주소를 카카오 주소 검색 API로 조회하여 도로명 주소, 지번 주소, 좌표, 법정동 코드 및 지역 정보를 반환합니다."
    )
    ApiResponse<ShopResDTO.ShopAddressSearchResponse> searchAddress(
            @RequestParam String address
    );

    @Operation(
            summary = "샵 등록",
            description = "어드민이 새로운 샵을 등록합니다."
    )
    ResponseEntity<ApiResponse<CreateShopResponse>> createShop(
            @RequestBody ShopReqDTO.CreateShopRequest request
    );
}
