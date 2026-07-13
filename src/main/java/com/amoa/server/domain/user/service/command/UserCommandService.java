package com.amoa.server.domain.user.service.command;

import com.amoa.server.domain.user.dto.respose.KakaoUserInfoResDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.enums.Role;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.util.JwtUtil;
import com.amoa.server.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCommandService {

    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    // 회원 탈퇴
    @Transactional
    public void withdrawalUser(Long userId, String accessToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(UserErrorCode.MEMBER_NOT_FOUND)
                );

        redisUtil.deleteRefreshToken(userId);
        setTokenBlackList(accessToken);

        /*
         * 소프트 삭제를 사용할 예정이라면 deleteById 대신
         * user.withdraw() 같은 상태 변경 메서드를 사용하는 편이 좋음.
         */
        userRepository.delete(user);
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId, String accessToken) {
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(UserErrorCode.MEMBER_NOT_FOUND)
                );

        redisUtil.deleteRefreshToken(userId);
        setTokenBlackList(accessToken);
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

        return userRepository.findBySocialUid(socialUid)
                .orElseGet(() -> {
                    User newUser = User.builder()
                        .socialUid(socialUid)
                        .email(email)
                        .nickname(nickname)
                        .role(Role.NEW_USER)
                        .isActive(true)
                        .build();

                    return userRepository.save(newUser);
        });
    }

    public void setTokenBlackList(String accessToken) {
        Long remainingTime = jwtUtil.getExpirationTime(accessToken);

        if (remainingTime > 0) {
            redisUtil.setBlackList(accessToken, remainingTime);
        }
    }
}