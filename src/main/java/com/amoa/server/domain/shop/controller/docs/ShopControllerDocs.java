package com.amoa.server.domain.shop.controller.docs;

import com.amoa.server.domain.common.enums.ArtType;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Shop(user)", description = "샵 관련 API(user)")
public interface ShopControllerDocs {

    @Operation(summary = "샵 상세 카드 목록 조회", description = "샵 상세 페이지에서 카드 목록을 조회합니다.")
    ApiResponse<ShopResDTO.CardListResponse> getShopCards(
            @PathVariable Long shopId,
            @RequestParam(required = false) ArtType artType,
            @RequestParam(defaultValue = "LATEST") SortType sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(summary = "샵 상세 조회", description = "유저가 샵 상세 정보를 조회합니다.")
    ApiResponse<ShopResDTO.ShopDetailResponse> getShopDetail(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails);

}