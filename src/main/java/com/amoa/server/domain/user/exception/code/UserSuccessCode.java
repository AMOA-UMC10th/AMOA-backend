package com.amoa.server.domain.user.exception.code;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {

    USER_FOUND(
            HttpStatus.OK,
            "USER200",
            "회원 조회에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
