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
            "KAKAO400_1",
            "카카오로부터 4XX 응답을 받았습니다."
    ),

    KAKAO_TOKEN_RESPONSE_EMPTY(
            HttpStatus.BAD_GATEWAY,
            "AUTH400_2",
            "카카오 토큰 응답이 비어있습니다."
    ),

    UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "AUTH401_1",
            "인증이 필요합니다."
    ),

    TOKEN_EXPIRED(
            HttpStatus.UNAUTHORIZED,
            "TOKEN401_2",
            "토큰이 만료되었습니다."
    ),

    TOKEN_INVALID(
            HttpStatus.UNAUTHORIZED,
            "TOKEN401_3",
            "토큰이 유효하지 않습니다."
    ),

    TOKEN_ERROR(
            HttpStatus.UNAUTHORIZED,
            "TOKEN401_4",
            "토큰에 문제가 있습니다."
    ),

    TOKEN_BLACKLIST(
            HttpStatus.UNAUTHORIZED,
            "TOKEN401_5",
            "블랙리스트에 포함된 토큰입니다."
    ),

    DEV_AUTH_KEY_INVALID(
            HttpStatus.UNAUTHORIZED,
            "AUTH401_6",
            "개발자 인증 키가 올바르지 않습니다."
    ),


    DEV_USER_NOT_ALLOWED(
            HttpStatus.FORBIDDEN,
            "AUTH403_1",
            "개발자용 토큰 발급이 허용되지 않은 사용자입니다."
    ),

    DEV_USER_ROLE_INVALID(
            HttpStatus.FORBIDDEN,
            "AUTH403_2",
            "USER 권한을 가진 사용자만 개발자용 토큰을 발급할 수 있습니다."
    ),

    KAKAO_5XX(
            HttpStatus.BAD_GATEWAY,
            "AUTH500_1",
            "카카오 서버와 통신하는 중 오류가 발생했습니다."
    ),

    KAKAO_RESPONSE_EMPTY(
            HttpStatus.BAD_GATEWAY,
            "AUTH500_2",
            "카카오 사용자 정보 응답이 비어 있습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
