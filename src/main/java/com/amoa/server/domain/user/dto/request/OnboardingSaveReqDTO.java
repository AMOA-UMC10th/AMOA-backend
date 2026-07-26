package com.amoa.server.domain.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record OnboardingSaveReqDTO(

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(
                min = 2,
                max = 10,
                message = "닉네임은 2자 이상 10자 이하여야 합니다."
        )
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]+$",
                message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다."
        )
        String nickname,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(
                regexp = "^010\\d{8}$",
                message = "전화번호 형식이 올바르지 않습니다."
        )
        String phoneNumber,

        @NotEmpty(message = "선호 디자인을 한 개 이상 선택해야 합니다.")
        @Size(min = 1, max = 3)
        List<@NotNull Long> designTagIds,

        @NotEmpty(message = "관심 지역을 한 개 이상 선택해야 합니다.")
        @Size(min = 1, max = 3)
        List<@NotNull Long> regionIds,

        @NotEmpty(message = "약관 동의 정보는 필수입니다.")
        List<
                @NotNull(message = "약관 동의 정보는 null일 수 없습니다.")
                @Valid AgreementRequest
                > agreements
) {

    public record AgreementRequest(

            @NotNull(message = "약관 ID는 필수입니다.")
            Long termId,

            boolean agreed
    ) {
    }
}