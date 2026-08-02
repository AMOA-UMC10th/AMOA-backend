package com.amoa.server.domain.user.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record UserProfileResDTO(

            Long userId,
            String profileImageUrl,
            String name,
            String email,
            String nickname,
            String phoneNumber,
            List<Long> selectedDesignTagIds,
            List<InterestedRegionDto> interestedRegions,
            List<NotificationSettingDto> notificationSettings

    ) {

    @Builder
    public record InterestedRegionDto(

            Long regionId,
            String region1DepthName,
            String region2DepthName,
            String region3DepthName

    ) {}

    @Builder
    public record NotificationSettingDto(

            String notificationType,
            String title,
            String description,
            boolean enabled

    ) {}
}
