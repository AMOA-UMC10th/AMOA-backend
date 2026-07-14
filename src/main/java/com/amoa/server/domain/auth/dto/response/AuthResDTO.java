package com.AMOA.server.domain.auth.dto.response;

import com.AMOA.server.domain.user.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 응답 json에서 제외
    @NoArgsConstructor
    public static class LoginResultDTO {
        private String email;
        private String nickname;
        private String accessToken;
        private String refreshToken;
        private String tempToken;
        private Role role;
        private Boolean isNewUser;
    }
}
