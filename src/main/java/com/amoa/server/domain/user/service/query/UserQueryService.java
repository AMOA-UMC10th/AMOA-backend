package com.amoa.server.domain.user.service.query;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.response.ShopResDTO;
import com.amoa.server.domain.user.converter.UserConverter;
import com.amoa.server.domain.user.dto.response.NicknameCheckResDTO;
import com.amoa.server.domain.user.dto.response.UserProfileResDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.entity.mapping.UserNotificationSetting;
import com.amoa.server.domain.user.entity.mapping.UserRegion;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserDesignTagRepository;
import com.amoa.server.domain.user.repository.UserInterestedRegionRepository;
import com.amoa.server.domain.user.repository.UserNotificationSettingRepository;
import com.amoa.server.domain.user.repository.UserRepository;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");

    private final UserRepository userRepository;
    private final UserInterestedRegionRepository userInterestedRegionRepository;
    private final UserDesignTagRepository userDesignTagRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final DesignTagRepository designTagRepository;

    public NicknameCheckResDTO checkNickname(String nickname) {
        if (nickname == null || !NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new UserException(UserErrorCode.NICKNAME_INVALID_FORMAT);
        }

        boolean available = !userRepository.existsByNickname(nickname);
        return UserConverter.toNicknameCheckResDTO(nickname, available);
    }

    public UserProfileResDTO getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(
                        UserErrorCode.USER_NOT_FOUND
                ));

        List<Long> selectedDesignTagIds =
                userDesignTagRepository.findAllByUser_Id(userId)
                        .stream()
                        .map(userDesignTag -> userDesignTag.getDesignTag().getId())
                        .toList();

        List<UserRegion> userRegions =
                userInterestedRegionRepository.findAllByUser_Id(userId);

        List<UserNotificationSetting> notificationSettings =
                userNotificationSettingRepository.findAllByUser_Id(userId);

        return UserConverter.toUserProfileResDTO(
                user,
                selectedDesignTagIds,
                userRegions,
                notificationSettings
        );
    }

    public ShopResDTO.DesignTagListResponse getDesignMoods() {
        List<DesignTag> designTags = designTagRepository.findAllByOrderByIdAsc();
        return ShopConverter.toDesignTagListResponse(designTags);
    }
}