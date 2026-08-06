package com.amoa.server.domain.user.dto.request;

import com.amoa.server.domain.user.enums.NotificationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserProfileUpdateReqDTO(

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

        @NotNull(message = "선호 디자인 목록은 필수입니다.")
        @Size(max = 7, message = "선호 디자인은 최대 7개까지 선택할 수 있습니다.")
        List<@NotNull Long> selectedDesignTagIds,

        @NotNull(message = "관심 지역 목록은 필수입니다.")
        @Size(max = 3, message = "관심 지역은 최대 3개까지 선택할 수 있습니다.")
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