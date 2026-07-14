package com.AMOA.server.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode{

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER404_1",
            "사용자를 찾을 수 없습니다."
    ),

    USER_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST,
            "USER400_1",
            "사용자가 이미 존재하고 있습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
