package com.amoa.server.domain.user.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode{

    NICKNAME_INVALID_FORMAT(
            HttpStatus.BAD_REQUEST,
            "ONBOARDING400_1",
            "닉네임은 한글/영문/숫자 2~10자만 사용할 수 있습니다."
    ),

    INTERESTED_REGION_LIMIT_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "USER400_2",
            "관심 지역은 최대 3개까지 선택할 수 있습니다."
    ),

    ONBOARDING_ALREADY_COMPLETED(
            HttpStatus.BAD_REQUEST,
            "USER400_3",
            "이미 온보딩을 완료한 사용자입니다."
    ),

    REQUIRED_TERM_NOT_AGREED(
            HttpStatus.BAD_REQUEST,
            "USER400_4",
            "필수 약관에 모두 동의해야 합니다."
    ),

    DUPLICATED_DESIGN_TAG(
            HttpStatus.BAD_REQUEST,
            "USER400_5",
            "중복된 디자인 태그가 포함되어 있습니다."
    ),

    DUPLICATED_INTERESTED_REGION(
            HttpStatus.BAD_REQUEST,
            "USER400_6",
            "중복된 관심 지역이 포함되어 있습니다."
    ),

    DUPLICATED_TERM_AGREEMENT(
            HttpStatus.BAD_REQUEST,
            "USER400_7",
            "중복된 약관 동의 정보가 포함되어 있습니다."
    ),

    PHONE_INVALID_FORMAT(
            HttpStatus.BAD_REQUEST,
            "USER400_8",
            "올바른 전화번호 형식이 아닙니다."
    ),

    PHONE_CODE_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "USER400_9",
            "인증번호가 만료되었거나 발송되지 않았습니다."
    ),

    PHONE_CODE_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "USER400_10",
            "인증번호가 일치하지 않습니다."
    ),

    PHONE_NOT_VERIFIED(
            HttpStatus.BAD_REQUEST,
            "USER400_11",
            "휴대폰 인증이 완료되지 않았습니다."
    ),

    USER_NOT_ACTIVE(
            HttpStatus.UNAUTHORIZED,
            "USER401_1",
            "탈퇴한 회원입니다."
    ),

    USER_UNAUTHORIZED(
            HttpStatus.UNAUTHORIZED,
            "USER401_2",
            "권한이 없습니다."
    ),

    PHONE_SEND_TOO_FREQUENT(
            HttpStatus.TOO_MANY_REQUESTS,
            "USER429_1",
            "잠시 후 다시 시도해주세요."
    ),

    PHONE_VERIFY_ATTEMPTS_EXCEEDED(
            HttpStatus.TOO_MANY_REQUESTS,
            "USER429_2",
            "인증 시도 횟수를 초과했습니다. 인증번호를 다시 요청해주세요."
    ),

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "USER404_1",
            "조회된 회원이 없습니다."
    ),

    DESIGN_TAG_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ONBOARDING404_2",
            "존재하지 않는 디자인 태그가 포함되어 있습니다."
    ),

    REGION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ONBOARDING404_3",
            "존재하지 않는 관심 지역이 포함되어 있습니다."
    ),

    NICKNAME_DUPLICATED(
            HttpStatus.CONFLICT,
            "ONBOARDING409_1",
            "이미 사용 중인 닉네임입니다."
    ),

    SMS_SEND_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
        "USER500_1",
                "인증번호 발송에 실패했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}