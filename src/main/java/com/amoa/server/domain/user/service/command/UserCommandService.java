package com.amoa.server.domain.user.service.command;

import com.amoa.server.domain.card.repository.UserCardRepository;
import com.amoa.server.domain.reservation.repository.ReservationRepository;
import com.amoa.server.domain.shop.repository.SavedShopRepository;
import com.amoa.server.domain.user.dto.request.PhoneVerifyReqDTO;
import com.amoa.server.domain.user.dto.response.KakaoUserInfoResDTO;
import com.amoa.server.domain.user.dto.response.PhoneVerifyResDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.enums.Role;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserAgreementRepository;
import com.amoa.server.domain.user.repository.UserDesignTagRepository;
import com.amoa.server.domain.user.repository.UserInterestedRegionRepository;
import com.amoa.server.domain.user.repository.UserNotificationSettingRepository;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.util.JwtUtil;
import com.amoa.server.global.util.RedisUtil;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.amoa.server.domain.user.dto.request.PhoneSendReqDTO;
import com.amoa.server.domain.user.dto.response.PhoneSendResDTO;
import com.amoa.server.global.sms.SmsSender;
import java.security.SecureRandom;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCommandService {

    private final SmsSender smsSender;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[0-9]{8,9}$");
    private static final Duration CODE_TTL = Duration.ofMinutes(3);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(30);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final UserCreateCommandService userCreateCommandService;
    private final JwtUtil jwtUtil;
    private static final int MAX_VERIFY_ATTEMPTS = 5;
    private static final Duration VERIFIED_STATE_TTL = Duration.ofMinutes(30);
    private final UserDesignTagRepository userDesignTagRepository;
    private final UserInterestedRegionRepository userInterestedRegionRepository;
    private final UserAgreementRepository userAgreementRepository;
    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final UserCardRepository userCardRepository;
    private final SavedShopRepository savedShopRepository;
    private final ReservationRepository reservationRepository;

    // 기존 회원 조회 또는 신규 회원 생성
    @Transactional
    public User getOrSaveMember(KakaoUserInfoResDTO kakaoUserInfo) {
        KakaoUserInfoResDTO.KakaoAccount account =
                kakaoUserInfo.getKakaoAccount();

        if (kakaoUserInfo.getId() == null || account == null) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }

        String socialUid = kakaoUserInfo.getId().toString();

        String email = account.getEmail() != null
                ? account.getEmail()
                : "kakao-" + socialUid + "@no-email.local";

        String userName =
                account.getProfile() != null
                        && account.getProfile().getUserName() != null
                        && !account.getProfile().getUserName().isBlank()
                        ? account.getProfile().getUserName()
                        : "익명사용자";

        String profileImageUrl =
                account.getProfile() != null
                        ? account.getProfile().getProfileImageUrl()
                        : null;

        return userRepository.findBySocialUidIncludingInactive(socialUid)
                .map(user -> {

                    if (!Boolean.TRUE.equals(user.getIsActive())) {
                        log.debug("탈퇴 회원 재가입 처리");
                        user.reactivate();
                    }

                    return user;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .socialUid(socialUid)
                            .email(email)
                            .userName(userName)
                            .profileImageUrl(profileImageUrl)
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
                            user.reactivate();
                        }

                        return user;
                    }
        });
    }

    // 회원 탈퇴
    @Transactional
    public void withdrawalUser(
            Long userId,
            String accessToken
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserException(UserErrorCode.USER_NOT_FOUND)
                );

        log.debug("회원 탈퇴 요청 처리, isActive={}", user.getIsActive());

        // Access Token 블랙리스트 등록
        Long remainingTime =
                jwtUtil.getExpirationTime(accessToken);

        // 1. 온보딩 관련 데이터 초기화
        userDesignTagRepository.deleteAllByUser_Id(userId);
        userInterestedRegionRepository.deleteAllByUser_Id(userId);
        userAgreementRepository.deleteAllByUser_Id(userId);
        userNotificationSettingRepository.deleteAllByUser_Id(userId);

        // 2. 찜 데이터 초기화
        userCardRepository.deleteAllByUser_Id(userId);
        savedShopRepository.deleteAllByUser_Id(userId);

        // 3. 기존 예약은 DB에 보존하되 사용자에게 미노출
        reservationRepository.hideAllByUserId(userId);

        // 4. 사용자 프로필 / 온보딩 상태 초기화
        user.resetForWithdrawal();

        // 5. Soft Delete
        // @SQLDelete -> is_active = false
        userRepository.delete(user);

        // 6. DB 커밋 성공 후 Redis 토큰 무효화
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        redisUtil.deleteRefreshToken(userId);

                        if (remainingTime > 0) {
                            redisUtil.saveBlackList(
                                    accessToken,
                                    Duration.ofMillis(remainingTime)
                            );
                        }
                    }
                }
        );
    }

    // 휴대폰 인증번호 발송
    public PhoneSendResDTO sendPhoneVerificationCode(Long userId, PhoneSendReqDTO request) {
        String phoneNumber = request.phoneNumber().replaceAll("[^0-9]", "");

        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new UserException(UserErrorCode.PHONE_INVALID_FORMAT);
        }

        boolean phoneAcquired = redisUtil.tryAcquirePhoneSendCooldown("phone:" + phoneNumber, RESEND_COOLDOWN);
        if (!phoneAcquired) {
            throw new UserException(UserErrorCode.PHONE_SEND_TOO_FREQUENT);
        }

        boolean userAcquired = redisUtil.tryAcquirePhoneSendCooldown("user:" + userId, RESEND_COOLDOWN);
        if (!userAcquired) {
            redisUtil.releasePhoneSendCooldown("phone:" + phoneNumber); // 전화번호 쪽 예약도 같이 풀어줌
            throw new UserException(UserErrorCode.PHONE_SEND_TOO_FREQUENT);
        }

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        try {
            smsSender.send(phoneNumber, "[AMOA] 인증번호는 [" + code + "]입니다.");
        } catch (RuntimeException e) {
            redisUtil.releasePhoneSendCooldown("phone:" + phoneNumber);
            redisUtil.releasePhoneSendCooldown("user:" + userId);
            throw e;
        }

        redisUtil.savePhoneVerificationCode(phoneNumber, code, CODE_TTL);

        return new PhoneSendResDTO((int) CODE_TTL.toSeconds());
    }

    // 휴대폰 인증번호 확인
    // UserCommandService.java
    public PhoneVerifyResDTO verifyPhoneCode(Long userId, PhoneVerifyReqDTO request) {
        String phoneNumber = request.phoneNumber().replaceAll("[^0-9]", "");

        String savedCode = redisUtil.getPhoneVerificationCode(phoneNumber);
        if (savedCode == null) {
            throw new UserException(UserErrorCode.PHONE_CODE_NOT_FOUND);
        }

        if (!savedCode.equals(request.code())) {
            long attempts = redisUtil.incrementPhoneVerifyAttempts(phoneNumber, CODE_TTL);
            if (attempts >= MAX_VERIFY_ATTEMPTS) {
                redisUtil.deletePhoneVerificationCode(phoneNumber);
                redisUtil.deletePhoneVerifyAttempts(phoneNumber);
                throw new UserException(UserErrorCode.PHONE_VERIFY_ATTEMPTS_EXCEEDED);
            }
            throw new UserException(UserErrorCode.PHONE_CODE_MISMATCH);
        }

        redisUtil.deletePhoneVerificationCode(phoneNumber);
        redisUtil.deletePhoneVerifyAttempts(phoneNumber);
        redisUtil.markPhoneVerified(userId + ":" + phoneNumber, VERIFIED_STATE_TTL);   // userId 같이 키에 포함

        return new PhoneVerifyResDTO(true);
    }
}