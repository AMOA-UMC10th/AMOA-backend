package com.AMOA.server.global.auth.filter;

import com.AMOA.server.domain.auth.exception.AuthException;
import com.AMOA.server.domain.auth.exception.code.AuthErrorCode;
import com.AMOA.server.global.apiPayload.ApiResponse;
import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import com.AMOA.server.global.apiPayload.code.GeneralErrorCode;
import com.AMOA.server.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("jwt 토큰이 만료되었습니다: {}", e.getMessage());
            setErrorResponse(response, AuthErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            log.warn("jwt 토큰이 유효하지 않습니다.  : {}", e.getMessage());
            setErrorResponse(response, AuthErrorCode.TOKEN_INVALID);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("유효하지 않은 jwt 토큰입니다: {}", e.getMessage());
            setErrorResponse(response, AuthErrorCode.TOKEN_INVALID);
        }catch (AuthException e) {
            log.warn("인증 예외 발생: {}", e.getMessage());
            setErrorResponse(response, e.getCode());
        } catch (GeneralException e) {
            log.warn("jwt 관련 예외 발생: {}", e.getMessage());
            setErrorResponse(response, e.getCode());
        } catch (Exception e) {
            log.error("예상치 못한 jwt filter chain 관련 예외입니다.", e);
            setErrorResponse(response, GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, BaseErrorCode code) throws IOException {
        response.setStatus(code.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // application/json을 의미
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> errorResponse = ApiResponse.onFailure(
                code,
                null
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}