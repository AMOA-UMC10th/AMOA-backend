package com.amoa.server.domain.user.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode{
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER404_1",
            "조회된 회원이 없습니다."),
    MEMBER_NOT_ACTIVE(HttpStatus.UNAUTHORIZED,
            "MEMBER401_1",
            "탈퇴한 회원입니다."),
    MEMBER_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "MEMBER401_2",
            "권한이 없습니다."),
    NICKNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST,
            "MEMBER400_1",
            "닉네임은 한글/영문/숫자 2~10자만 사용할 수 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}