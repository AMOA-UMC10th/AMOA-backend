package com.AMOA.server.domain.shop.controller.docs;

import com.AMOA.server.domain.shop.dto.Request.ShopReqDTO;
import com.AMOA.server.domain.shop.dto.Response.ShopResDTO;
import com.AMOA.server.domain.shop.dto.Response.ShopResDTO.CreateShopResponse;
import com.AMOA.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Shop", description = "샵 관련 API")
public interface ShopControllerDocs {

    @Operation(summary = "디자인태그 목록 조회", description = "샵 등록 시 선택 가능한 디자인태그 목록을 조회합니다.")
    ApiResponse<ShopResDTO.DesignTagListResponse> getDesignTags();

    @Operation(summary = "샵 이름으로 카카오 로컬 API 검색", description = "샵 이름으로 카카오 로컬 API를 검색하여 주소, 전화번호를 자동완성합니다.")
    ApiResponse<ShopResDTO.KakaoSearchResponse> searchShopByKeyword(@RequestParam String keyword);

    @Operation(summary = "샵 등록", description = "어드민이 새로운 샵을 등록합니다.")
    ResponseEntity<ApiResponse<CreateShopResponse>> createShop(
            @RequestBody ShopReqDTO.CreateShopRequest request);
}