package com.amoa.server.domain.user.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {

    AUTH_LOGIN_OK(
            HttpStatus.OK,
            "USER200_1",
            "로그인에 성공하였습니다."
    ),

    USER_LIKED_SHOPS_SUCCESS(
            HttpStatus.OK,
            "USER200_2",
            "찜한 네일샵 목록 조회에 성공했습니다."
    ),

    NICKNAME_CHECK_SUCCESS(
            HttpStatus.OK,
            "USER200_3",
            "닉네임 사용 가능 여부 조회에 성공했습니다."
    ),

    USER_WITHDRAW_SUCCESS(
            HttpStatus.OK,
            "USER200_4",
            "탈퇴에 성공하였습니다."
    ),

    USER_PROFILE_GET_OK(
            HttpStatus.OK,
            "USER200_5",
            "사용자 정보 조회에 성공했습니다."
    ),

    SAVED_PLACE_CREATE_OK(
            HttpStatus.OK,
            "USER201_1",
            "장소가 성공적으로 저장되었습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
