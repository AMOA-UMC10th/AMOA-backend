package com.amoa.server.domain.user.service.command;

import com.amoa.server.domain.user.dto.respose.KakaoUserInfoResDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.enums.Role;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import com.amoa.server.global.util.JwtUtil;
import com.amoa.server.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCommandService {

    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final UserCreateCommandService userCreateCommandService;
    private final JwtUtil jwtUtil;

    // 회원 탈퇴
    @Transactional
    public void withdrawalUser(Long userId, String accessToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(UserErrorCode.MEMBER_NOT_FOUND)
                );

        redisUtil.deleteRefreshToken(userId);
        redisUtil.setBlackList(
                accessToken,
                jwtUtil.getExpirationTime(accessToken);

        userRepository.delete(user);
    }

    // 기존 회원 조회 또는 신규 회원 생성
    @Transactional
    public User getOrSaveMember(KakaoUserInfoResDTO kakaoUserInfo) {
        KakaoUserInfoResDTO.KakaoAccount account =
                kakaoUserInfo.getKakaoAccount();

        if (kakaoUserInfo.getId() == null || account == null) {
            throw new UserException(UserErrorCode.MEMBER_NOT_FOUND);
        }

        String socialUid = kakaoUserInfo.getId().toString();

        String email = account.getEmail() != null
                ? account.getEmail()
                : "kakao-" + socialUid + "@no-email.local";

        String nickname =
                account.getProfile() != null
                        && account.getProfile().getNickname() != null
                        && !account.getProfile().getNickname().isBlank()
                        ? account.getProfile().getNickname()
                        : "익명사용자";

        return userRepository.findBySocialUidIncludingInactive(socialUid)
                .map(user -> {
                    if (!Boolean.TRUE.equals(user.getIsActive())) {
                        throw new GeneralException(
                                UserErrorCode.MEMBER_UNAUTHORIZED
                        );
                    }

                    return user;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                        .socialUid(socialUid)
                        .email(email)
                        .nickname(nickname)
                        .role(Role.NEW_USER)
                        .isActive(true)
                        .build();
                    try {
                        return userCreateCommandService.saveAndFlush(newUser);
                    } catch (DataIntegrityViolationException exception) {
                        User user = userRepository
                                .findBySocialUidIncludingInactive(socialUid)
                                .orElseThrow(() -> exception);

                        if (!Boolean.TRUE.equals(user.getIsActive())) {
                            throw new GeneralException(
                                    UserErrorCode.MEMBER_UNAUTHORIZED
                            );
                        }

                        return user;
                    }
        });
    }
}