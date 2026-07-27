package com.amoa.server.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PhoneSendReqDTO(
        @NotBlank(message = "전화번호는 필수입니다.")
        String phoneNumber
) {
}