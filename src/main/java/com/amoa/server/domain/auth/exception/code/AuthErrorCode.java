package com.amoa.server.domain.auth.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    KAKAO_4XX(
            HttpStatus.BAD_REQUEST,
            "카카오로부터 4XX 응답을 받았습니다.",
            "KAKAO4XX"),
    KAKAO_5XX(
            HttpStatus.BAD_GATEWAY,
            "카카오 서버와 통신하는 중 오류가 발생했습니다.",
            "AUTH500_1"),
    KAKAO_RESPONSE_EMPTY(
            HttpStatus.BAD_GATEWAY,
            "카카오 사용자 정보 응답이 비어 있습니다.",
            "AUTH500_2"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "인증이 필요합니다.",
            "AUTH401_1"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,
            "토큰이 만료되었습니다.",
            "TOKEN401_2"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED,
            "토큰이 유효하지 않습니다.",
            "TOKEN401_3"),
    TOKEN_ERROR(HttpStatus.UNAUTHORIZED,
            "토큰에 문제가 있습니다.",
            "TOKEN401_4"),
    TOKEN_BLACKLIST(HttpStatus.UNAUTHORIZED,
            "블랙리스트에 포함된 토큰입니다.",
            "TOKEN401_5"),
    KAKAO_TOKEN_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY,
            "카카오 토큰 응답이 비어있습니다.",
            "AUTH400_1");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
