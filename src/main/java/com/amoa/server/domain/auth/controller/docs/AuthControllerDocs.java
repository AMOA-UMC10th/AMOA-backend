package com.amoa.server.domain.auth.controller.docs;

import com.amoa.server.domain.auth.dto.request.AuthReqDTO;
import com.amoa.server.domain.auth.dto.response.AuthResDTO;
import com.amoa.server.domain.auth.dto.response.AuthResDTO.LoginResultDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthControllerDocs {
    @Operation(
            summary = "카카오 소셜 로그인",
            description = "카카오 액세스 토큰을 보내면 회원 정보를 반환합니다."
    )
    ApiResponse<AuthResDTO.LoginResultDTO> kakaoLogin(
            @RequestBody AuthReqDTO.KakaoLoginRequestDTO request
    );

    @Operation(
            summary = "액세스 토큰 재발행",
            description = "리프레쉬 토큰을 보내면 새 액세스 토큰을 재발행합니다."
    )
    ApiResponse<LoginResultDTO> reissueToken(
            @RequestBody AuthReqDTO.ReissueRequestDTO request
    );
}
