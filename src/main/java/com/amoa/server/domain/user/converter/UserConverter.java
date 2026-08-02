package com.amoa.server.domain.user.converter;

import com.amoa.server.domain.user.dto.response.NicknameCheckResDTO;
import com.amoa.server.domain.user.dto.response.UserProfileResDTO;
import com.amoa.server.domain.user.dto.response.UserProfileResDTO.InterestedRegionDto;
import com.amoa.server.domain.user.dto.response.UserProfileResDTO.NotificationSettingDto;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.entity.mapping.UserNotificationSetting;
import com.amoa.server.domain.user.entity.mapping.UserRegion;
import com.amoa.server.domain.user.enums.NotificationType;
import java.util.List;

public class UserConverter {

    public static NicknameCheckResDTO toNicknameCheckResDTO(String nickname, boolean available) {
        return new NicknameCheckResDTO(nickname, available);
    }

    public static UserProfileResDTO toUserProfileResDTO(
            User user,
            List<Long> selectedDesignTagIds,
            List<UserRegion> userRegions,
            List<UserNotificationSetting> notificationSettings
    ) {
        return UserProfileResDTO.builder()
                .userId(user.getId())
                .name(user.getUserName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phoneNumber(user.getUserPhoneNumber())
                .selectedDesignTagIds(selectedDesignTagIds)
                .interestedRegions(
                        userRegions.stream()
                                .map(UserConverter::toInterestedRegionDto)
                                .toList()
                )
                .notificationSettings(
                        notificationSettings.stream()
                                .map(UserConverter::toNotificationSettingDto)
                                .toList()
                )
                .build();
    }

    private static InterestedRegionDto toInterestedRegionDto(
            UserRegion userRegion
    ) {
        return InterestedRegionDto.builder()
                .regionId(userRegion.getRegion().getId())
                .region1DepthName(userRegion.getRegion().getFirstDepth())
                .region2DepthName(userRegion.getRegion().getSecondDepth())
                .region3DepthName(userRegion.getRegion().getThirdDepth())
                .build();
    }

    private static NotificationSettingDto toNotificationSettingDto(
            UserNotificationSetting setting
    ) {
        return NotificationSettingDto.builder()
                .notificationType(setting.getNotificationType().name())
                .title(getNotificationTitle(setting.getNotificationType()))
                .description(getNotificationDescription(setting.getNotificationType()))
                .enabled(setting.isEnabled())
                .build();
    }

    private static String getNotificationTitle(NotificationType type) {
        return switch (type) {
            case MARKETING -> "마케팅 정보 수신";
            case RESERVATION -> "예약 알림";
            case EVENT -> "이벤트 알림";
        };
    }

    private static String getNotificationDescription(NotificationType type) {
        return switch (type) {
            case MARKETING -> "이벤트 및 혜택 정보를 받아볼 수 있습니다.";
            case RESERVATION -> "예약 일정과 상태 변경 안내를 받아볼 수 있습니다.";
            case EVENT -> "이벤트 및 프로모션 안내를 받아볼 수 있습니다.";
        };
    }
}