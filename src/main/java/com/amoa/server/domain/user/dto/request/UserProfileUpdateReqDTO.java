package com.amoa.server.domain.user.dto.request;

import com.amoa.server.domain.user.enums.NotificationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserProfileUpdateReqDTO(

        String profileImageUrl,

        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해야 합니다.")
        String nickname,

        @Pattern(
                regexp = "^010\\d{8}$",
                message = "전화번호는 010으로 시작하는 11자리 숫자여야 합니다."
        )
        String phoneNumber,

        @Size(max = 3, message = "관심 디자인은 최대 3개까지 선택할 수 있습니다.")
        List<Long> selectedDesignTagIds,

        @Size(max = 3, message = "관심 지역은 최대 3개까지 선택할 수 있습니다.")
        List<Long> interestedRegionIds,

        @Valid
        List<@NotNull NotificationSettingUpdateRequest> notificationSettings
) {

    public record NotificationSettingUpdateRequest(
            NotificationType notificationType,
            Boolean enabled
    ) {
    }
}