package com.amoa.server.domain.user.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {
    AUTH_LOGIN_OK(HttpStatus.OK,
            "MEMBER200_1",
            "성공적으로 로그인에 성공하였습니다."
    ),
    SAVED_PLACE_CREATE_OK(
            HttpStatus.OK,
            "MEMBER201_1",
            "장소가 성공적으로 저장되었습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
