package com.amoa.server.global.auth.filter;

import com.amoa.server.domain.auth.exception.AuthException;
import com.amoa.server.domain.auth.exception.code.AuthErrorCode;
import com.amoa.server.global.auth.CustomUserDetailsService;
import com.amoa.server.global.util.JwtUtil;
import com.amoa.server.global.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Set<String> TEMP_TOKEN_ALLOWED_URIS = Set.of(
            "/api/v1/users/onboarding",
            "/api/v1/users/nickname/check",
            "/api/v1/users/phone/verification",
            "/api/v1/users/phone/verification/verify",
            "/api/v1/regions",
            "/api/v1/regions/present",
            "/api/v1/design-tags"
    );

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");

        String token = null;
        Long userId = null;

        // Bearer로 시작하는 토큰이 있는지 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();

            //서명, issuer?, 만료 검증
            Claims claims = jwtUtil.getClaimsFromToken(token);

            String category = claims.get("category", String.class);
            String role = claims.get("role", String.class);
            String requestUri =
                    request.getRequestURI()
                            .substring(request.getContextPath().length());

            logger.info(
                    "JWT 인증 확인 - subject: " + claims.getSubject()
                            + ", category: " + category
                            + ", role: " + role
                            + ", uri: " + requestUri
            );

            boolean validCategory =
                    TEMP_TOKEN_ALLOWED_URIS.contains(requestUri)
                            ? "temp".equals(category) || "access".equals(category)
                            : "access".equals(category);

            if (!validCategory) {
                logger.warn(
                        "허용되지 않은 토큰으로 인증을 시도했습니다. "
                                + "category: " + category
                                + ", uri: " + requestUri
                );

                throw new AuthException(
                        AuthErrorCode.TOKEN_INVALID
                );
            }

            // 로그아웃 처리된 Access Token인지 확인 -> Redis에서만 조회 가능
            if ("access".equals(category)
                    && redisUtil.isBlackListed(token)) {

                logger.warn(
                        "블랙리스트에 포함된 토큰으로 인증을 시도했습니다."
                );

                throw new AuthException(
                        AuthErrorCode.TOKEN_BLACKLIST
                );
            }

            userId =
                    Long.parseLong(claims.getSubject());
        }

        // 토큰이 유효하고, SecurityContext에 인증 정보가 없는 경우에 인증 처리
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 사용자 정보 조회
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());

            // 토큰 유효성 및 사용자 활성 여부 확인
            if (jwtUtil.validateToken(token) && userDetails.isEnabled()) {
                // Spring Security가 사용할 인증 토큰 생성
                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                        userDetails, // 사용자 정보
                        null, // 비밀번호(사용X)
                        userDetails.getAuthorities() // 권한 목록
                );

                // 인증 완료 후 SecurityContextHolder에 넣기
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
