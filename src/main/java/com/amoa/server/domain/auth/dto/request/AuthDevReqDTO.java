package com.amoa.server.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthDevReqDTO {

    @Schema(description = "개발자용 Access Token 발급 요청")
    public record DevTokenRequest(
            @NotNull(message = "사용자 ID는 필수입니다.")
            @Schema(
                    description = "개발용 Access Token을 발급받을 사용자 ID",
                    example = "1"
            )
            Long userId
    ) {
    }
}
