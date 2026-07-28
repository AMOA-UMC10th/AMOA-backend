package com.amoa.server.domain.user.dto.request;

import com.amoa.server.domain.user.enums.NotificationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserProfileUpdateReqDTO(

        String profileImageUrl,

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

        @Pattern(
                regexp = "^010\\d{8}$",
                message = "전화번호는 010으로 시작하는 11자리 숫자여야 합니다."
        )
        String phoneNumber,

        @NotEmpty(message = "선호 디자인을 한 개 이상 선택해야 합니다.")
        @Size(min = 1, max = 3)
        List<@NotNull Long> selectedDesignTagIds,

        @NotEmpty(message = "관심 지역을 한 개 이상 선택해야 합니다.")
        @Size(min = 1, max = 3)
        List<@NotNull Long> interestedRegionIds,

        @Valid
        List<@NotNull NotificationSettingUpdateRequest> notificationSettings
) {

    public record NotificationSettingUpdateRequest(
            NotificationType notificationType,
            Boolean enabled
    ) {
    }
}