package com.amoa.server.domain.auth.controller.docs;

import com.amoa.server.domain.auth.dto.request.AuthReqDTO;
import com.amoa.server.domain.auth.dto.response.AuthResDTO;
import com.amoa.server.domain.auth.dto.response.AuthResDTO.LoginResultDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthControllerDocs {
    @Operation(
            summary = "카카오 소셜 로그인",
            description = "카카오 액세스 토큰을 전달받아 로그인하고, 사용자 정보와 Access Token 및 Refresh Token을 발급합니다.",
            security = {}
    )
    ApiResponse<AuthResDTO.LoginResultDTO> kakaoLogin(
            @RequestBody AuthReqDTO.KakaoLoginRequestDTO request
    );

    @Operation(
            summary = "액세스 토큰 재발행",
            description = "리프레쉬 토큰을 보내면 새 액세스 토큰을 재발행합니다.",
            security = {}
    )
    ApiResponse<LoginResultDTO> reissueToken(
            @RequestBody AuthReqDTO.ReissueRequestDTO request
    );
}
