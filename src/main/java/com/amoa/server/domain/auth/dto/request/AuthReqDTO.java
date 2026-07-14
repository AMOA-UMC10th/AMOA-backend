package com.AMOA.server.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthReqDTO {

    // 액세스 토큰으로 카카오 로그인 요청 시
    @Getter
    @NoArgsConstructor
    public static class KakaoLoginRequestDTO {

        @NotBlank(message = "카카오 액세스 토큰은 필수입니다.")
        private String accessToken;
    }

    // 리프레쉬 토큰으로 액세스 토큰 재발급 요청 시
    @Getter
    @NoArgsConstructor
    public static class ReissueRequestDTO {
        private String refreshToken;
    }
}
