package com.amoa.server.domain.user.controller.docs;

import com.amoa.server.domain.shop.converter.SavedShopConverter;
import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "User", description = "유저 관련 API")
public interface UserControllerDocs {

    @Operation(
            summary = "회원 탈퇴 API",
            description = "로그인한 회원을 탈퇴 처리합니다. 동일한 카카오 계정으로 다시 로그인하면 재가입이 가능합니다."
    )
    ApiResponse<String> withdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request
    );

    @Operation(
            summary = "찜한 네일샵 목록 조회 API",
            description = "로그인한 사용자가 찜한 네일샵 목록을 조회합니다."
    )
    ApiResponse<SavedShopResDTO.LikedShopListResponse> getLikedShops(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    );

}
