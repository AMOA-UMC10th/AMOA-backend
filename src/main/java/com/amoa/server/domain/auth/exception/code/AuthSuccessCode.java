package com.amoa.server.domain.auth.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthSuccessCode implements BaseSuccessCode {
    AUTH_LOGIN_OK(HttpStatus.OK,
            "로그인을 완료하였습니다",
            "AUTH200_1"),
    AUTH_REISSUE_OK(HttpStatus.OK,
            "토큰 재발행을 완료하였습니다.",
            "AUTH200_2");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
