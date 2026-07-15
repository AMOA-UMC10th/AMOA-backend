package com.amoa.server.domain.user.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode{
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "조회된 회원이 없습니다.",
            "MEMBER404_1"),
    MEMBER_NOT_ACTIVE(HttpStatus.UNAUTHORIZED,
            "탈퇴한 회원입니다.",
            "MEMBER401_1"),
    MEMBER_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "권한이 없습니다.",
            "MEMBER401_2"
    );

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}