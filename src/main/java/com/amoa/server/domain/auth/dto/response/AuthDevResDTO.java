package com.amoa.server.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDevResDTO {

    @Schema(description = "개발자용 Access Token 발급 응답")
    public record DevTokenResponse(

            @Schema(
                    description = "개발자용 Access Token"
            )
            String accessToken,

            @Schema(
                    description = "토큰 만료 시간(초)"
            )
            long expiresIn
    ) {
    }
}
