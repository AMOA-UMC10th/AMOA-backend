package com.amoa.server.domain.shop.controller.docs;

import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface SavedShopControllerDocs {

    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(
            summary = "샵 찜 등록",
            description = "사용자가 특정 샵을 찜합니다."
    )
    ApiResponse<SavedShopResDTO.LikeResultDTO> createShopLike(
            @Parameter(description = "샵 ID")
            Long shopId,
            @Parameter(hidden = true)
            CustomUserDetails customUserDetails
    );

    @SecurityRequirement(name = "JWT TOKEN")
    @Operation(
            summary = "샵 찜 취소",
            description = "사용자가 찜한 샵을 취소합니다."
    )
    ApiResponse<Void> deleteShopLike(
            @Parameter(description = "샵 ID")
            Long shopId,
            @Parameter(hidden = true)
            CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "찜한 샵 목록 조회",
            description = "현재 로그인한 사용자가 찜한 샵 목록을 조회합니다."
    )
    ApiResponse<SavedShopResDTO.LikedShopListResponse> getLikedShops(
            CustomUserDetails customUserDetails,
            Pageable pageable
    );

}