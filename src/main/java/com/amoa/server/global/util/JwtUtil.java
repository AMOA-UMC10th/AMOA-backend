package com.AMOA.server.global.util;

import com.AMOA.server.domain.user.enums.Role;
import com.AMOA.server.global.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final String issuer;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;
    private final Duration tempExpiration;
    private final Duration devExpiration;

    public JwtUtil(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.issuer = jwtProperties.getIssuer();
        this.accessExpiration = Duration.ofMillis(jwtProperties.getAccessToken().getExpirationTime());
        this.refreshExpiration = Duration.ofMillis(jwtProperties.getRefreshToken().getExpirationTime());
        this.tempExpiration = Duration.ofMillis(jwtProperties.getTempToken().getExpirationTime());
        this.devExpiration = Duration.ofMillis(jwtProperties.getDevToken().getExpirationTime());
    }

    // 토큰 생성
    private String createToken(Long memberId, Duration accessExpiration, Role role, String category) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessExpiration.toMillis());

        // 표준 클레임만 작성하였고, 비공개 클레임은 claim 메서드로 정보를 추가 ex) .claim("role", "USER")
        return Jwts.builder()
                .subject(memberId.toString()) // 해당 토큰의 주체(memberId)
                .issuer(issuer)
                .issuedAt(Date.from(now.toInstant())) // 언제 발급했는지
                .expiration(validity)
                .signWith(secretKey)
                .claim("role", role.name())
                .claim("category", category)
                .compact();
    }

    // 토큰이 유효한지 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token. type={}", e.getClass().getSimpleName());
            return false;
        }
    }

    // 토큰에서 클레임을 추출하는 메서드
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getExpirationTime(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().getExpiration();

        long now = new Date().getTime();

        // 현재 시간과의 차이를 반환
        return expiration.getTime() - now;
    }

    // 액세스 토큰 생성
    public String createAccessToken(Long memberId, Role role) {
        return createToken(memberId, accessExpiration, role, "access");
    }

    // 리프레쉬 토큰 생성
    public String createRefreshToken(Long memberId) {
        return createToken(memberId, refreshExpiration, Role.USER, "refresh");
    }

    // 신규 회원용 임시 토큰 생성
    public String createTempToken(Long memberId) {
        return createToken(memberId, tempExpiration, Role.NEW_USER, "temp");
    }

    // 개발자 전용 임시 토큰 생성
    public String createDevToken(Long memberId) {
        return createToken(memberId, devExpiration, Role.USER, "access");
    }
}