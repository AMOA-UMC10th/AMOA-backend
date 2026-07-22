package com.amoa.server.domain.user.service.command;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.common.repository.RegionRepository;
import com.amoa.server.domain.user.dto.request.UserProfileUpdateReqDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.entity.mapping.UserDesignTag;
import com.amoa.server.domain.user.entity.mapping.UserNotificationSetting;
import com.amoa.server.domain.user.entity.mapping.UserRegion;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserDesignTagRepository;
import com.amoa.server.domain.user.repository.UserInterestedRegionRepository;
import com.amoa.server.domain.user.repository.UserNotificationSettingRepository;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileCommandService {

    private final UserRepository userRepository;
    private final DesignTagRepository designTagRepository;
    private final RegionRepository regionRepository;
    private final UserDesignTagRepository userDesignTagRepository;
    private final UserInterestedRegionRepository userInterestedRegionRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;

    @Transactional
    public void updateUserProfile(
            Long userId,
            UserProfileUpdateReqDTO request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new GeneralException(UserErrorCode.USER_NOT_FOUND)
                );

        updateProfileImage(user, request.profileImageUrl());
        updateNickname(user, request.nickname());
        updatePhoneNumber(user, request.phoneNumber());
        replaceDesignTags(user, request.selectedDesignTagIds());
        replaceInterestedRegions(
                user,
                request.interestedRegionIds()
        );
        updateNotificationSettings(
                user,
                request.notificationSettings()
        );
    }


    private void updateProfileImage(
            User user,
            String profileImageUrl
    ) {
        if (profileImageUrl == null) {
            return;
        }

        user.updateProfileImageUrl(profileImageUrl);
    }

    private void updateNickname(User user, String nickname) {
        if (nickname == null) {
            return;
        }

        boolean duplicated =
                userRepository.existsByNicknameAndIdNot(
                        nickname,
                        user.getId()
                );

        if (duplicated) {
            throw new GeneralException(
                    UserErrorCode.NICKNAME_DUPLICATED
            );
        }

        user.updateNickname(nickname);
    }

    private void updatePhoneNumber(
            User user,
            String phoneNumber
    ) {
        if (phoneNumber == null) {
            return;
        }

        user.updatePhoneNumber(phoneNumber);
    }

    private void replaceDesignTags(
            User user,
            List<Long> designTagIds
    ) {
        if (designTagIds == null) {
            return;
        }

        List<Long> distinctIds = designTagIds.stream()
                .distinct()
                .toList();

        List<DesignTag> designTags =
                designTagRepository.findAllById(distinctIds);

        if (designTags.size() != distinctIds.size()) {
            throw new GeneralException(
                    UserErrorCode.DESIGN_TAG_NOT_FOUND
            );
        }

        userDesignTagRepository.deleteAllByUser_Id(
                user.getId()
        );

        // 기존 매핑 삭제 SQL을 DB에 먼저 반영
        userDesignTagRepository.flush();

        List<UserDesignTag> mappings = designTags.stream()
                .map(designTag ->
                        UserDesignTag.create(user, designTag)
                )
                .toList();

        userDesignTagRepository.saveAll(mappings);
    }

    private void replaceInterestedRegions(
            User user,
            List<Long> regionIds
    ) {
        if (regionIds == null) {
            return;
        }

        List<Long> distinctRegionIds = regionIds.stream()
                .distinct()
                .toList();

        if (distinctRegionIds.size() > 3) {
            throw new GeneralException(
                    UserErrorCode.INTERESTED_REGION_LIMIT_EXCEEDED
            );
        }

        List<Region> regions =
                regionRepository.findAllById(distinctRegionIds);

        if (regions.size() != distinctRegionIds.size()) {
            throw new GeneralException(
                    UserErrorCode.REGION_NOT_FOUND
            );
        }

        userInterestedRegionRepository.deleteAllByUser_Id(user.getId());

        userInterestedRegionRepository.flush();

        List<UserRegion> mappings = regions.stream()
                .map(region -> UserRegion.create(user, region))
                .toList();

        userInterestedRegionRepository.saveAll(mappings);
    }

    private void updateNotificationSettings(
            User user,
            List<UserProfileUpdateReqDTO
                    .NotificationSettingUpdateRequest> requests
    ) {
        if (requests == null) {
            return;
        }

        for (var request : requests) {
            UserNotificationSetting setting =
                    userNotificationSettingRepository
                            .findByUser_IdAndNotificationType(
                                    user.getId(),
                                    request.notificationType()
                            )
                            .orElseGet(() ->
                                    UserNotificationSetting.create(
                                            user,
                                            request.notificationType(),
                                            request.enabled()
                                    )
                            );

            setting.updateEnabled(request.enabled());

            userNotificationSettingRepository.save(setting);
        }
    }
}