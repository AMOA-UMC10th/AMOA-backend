package com.amoa.server.domain.auth.service.command;

import com.amoa.server.domain.auth.converter.AuthConverter;
import com.amoa.server.domain.auth.dto.response.AuthResDTO;
import com.amoa.server.domain.auth.exception.AuthException;
import com.amoa.server.domain.auth.exception.code.AuthErrorCode;
import com.amoa.server.domain.auth.service.query.KakaoApiQueryService;
import com.amoa.server.domain.user.dto.respose.KakaoUserInfoResDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.enums.Role;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.domain.user.service.command.UserCommandService;
import com.amoa.server.global.config.JwtProperties;
import com.amoa.server.global.util.JwtUtil;
import com.amoa.server.global.util.RedisUtil;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCommandService {
    private final UserRepository userRepository;
    private final KakaoApiQueryService kakaoApiQueryService;
    private final UserCommandService userCommandService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResDTO.LoginResultDTO loginWithKakao(String kakaoAccessToken) {

        // 카카오 API로 사용자 정보 조회
        KakaoUserInfoResDTO kakaoUserInfoResDTO = kakaoApiQueryService.getUserInfo(kakaoAccessToken);

        User user = userCommandService.getOrSaveMember(kakaoUserInfoResDTO);

        // 탈퇴 또는 비활성 회원 로그인 차단
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UserException(UserErrorCode.MEMBER_UNAUTHORIZED);
        }

        // 온보딩 미완료 회원일 경우 임시 토큰 발급
        if (user.getRole() == Role.NEW_USER) {
            String tempToken = jwtUtil.createTempToken(user.getId());

            return AuthConverter.toNewMemberDTO(user, tempToken);
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        // 리프레쉬 토큰 업데이트
        redisUtil.saveRefreshToken(
                user.getId(),
                refreshToken,
                Duration.ofMillis(
                        jwtProperties
                                .getRefreshToken()
                                .getExpirationTime()
                )
        );

        return AuthConverter.toExistingMemberDTO(user, accessToken, refreshToken);
    }

    // 리프레쉬 토큰으로 액세스 토큰 재발행 로직
    @Transactional
    public AuthResDTO.LoginResultDTO reissueToken(String refreshToken) {
        // 리프레쉬 토큰 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // 토큰에서 클레임 정보 추출
        Claims claims = jwtUtil.getClaimsFromToken(refreshToken);
        String category = claims.get("category", String.class);

        if (category == null || !category.equals("refresh")) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        Long userId = Long.parseLong(claims.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.MEMBER_NOT_FOUND));

        // 탈퇴 또는 비활성 회원 토큰 재발행 불가
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new UserException(UserErrorCode.MEMBER_UNAUTHORIZED);
        }

        // 온보딩 미완료 회원은 토큰 재발행 불가
        if (user.getRole() == Role.NEW_USER) {
            throw new UserException(UserErrorCode.MEMBER_UNAUTHORIZED);
        }

        // 데이터베이스에 저장된 리프레쉬 토큰과 일치하는지 확인
        String savedRefreshToken =
                redisUtil.getRefreshToken(userId);

        if (savedRefreshToken == null
                || !savedRefreshToken.equals(refreshToken)) {
            // 토큰이 일치하지 않다면 다른 곳에서 이미 재발급에 사용되어 탈취 가능성 의심
            throw new AuthException(
                    AuthErrorCode.TOKEN_INVALID
            );
        }

        // 새로운 액세스 토큰과 리프레쉬 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getId());

        redisUtil.saveRefreshToken(
                user.getId(),
                newRefreshToken,
                Duration.ofMillis(
                        jwtProperties
                                .getRefreshToken()
                                .getExpirationTime()
                )
        );

        return AuthConverter.toExistingMemberDTO(user, newAccessToken, newRefreshToken);
    }
}
