package com.amoa.server.domain.auth.controller;

import com.amoa.server.domain.auth.controller.docs.AuthControllerDocs;
import com.amoa.server.domain.auth.dto.request.AuthReqDTO;
import com.amoa.server.domain.auth.dto.response.AuthResDTO;
import com.amoa.server.domain.auth.exception.code.AuthSuccessCode;
import com.amoa.server.domain.auth.service.command.AuthCommandService;
import com.amoa.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {
    private final AuthCommandService authCommandService;

    @Override
    @PostMapping("/kakao")
    public ApiResponse<AuthResDTO.LoginResultDTO> kakaoLogin(
           @Valid @RequestBody AuthReqDTO.KakaoLoginRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                AuthSuccessCode.AUTH_LOGIN_OK,
                authCommandService.loginWithKakao(
                        request.getAccessToken()
                )
        );
    }

    @Override
    @PostMapping("/reissue")
    public ApiResponse<AuthResDTO.LoginResultDTO> reissueToken(
            @RequestBody AuthReqDTO.ReissueRequestDTO request
    ) {
        return ApiResponse.onSuccess(
                AuthSuccessCode.AUTH_REISSUE_OK,
                authCommandService.reissueToken(request.getRefreshToken())
        );
    }
}