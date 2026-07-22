package com.amoa.server.domain.user.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode{
    USER_NOT_ACTIVE(
            HttpStatus.UNAUTHORIZED,
            "MEMBER401_1",
            "탈퇴한 회원입니다."
    ),

    USER_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "MEMBER401_2",
            "권한이 없습니다."
    ),

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "MEMBER404_1",
            "조회된 회원이 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}