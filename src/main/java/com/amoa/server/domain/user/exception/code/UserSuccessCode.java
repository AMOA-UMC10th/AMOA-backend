package com.amoa.server.domain.user.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {
    AUTH_LOGIN_OK(HttpStatus.OK,
            "로그인에 성공하였습니다.",
            "USER200_1"
    ),

    SAVED_PLACE_CREATE_OK(
            HttpStatus.OK,
            "장소가 성공적으로 저장되었습니다.",
            "USER201_1"
    ),

    USER_WITHDRAW_SUCCESS(
            HttpStatus.OK,
            "탈퇴에 성공하였습니다.",
            "USER200_2"
    );

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
