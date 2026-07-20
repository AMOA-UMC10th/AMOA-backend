package com.amoa.server.domain.reservation.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {

    CARD_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESERVATION404_1",
            "아트 카드를 찾을 수 없습니다."
    ),

    SHOP_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESERVATION404_2",
            "샵을 찾을 수 없습니다."
    ),

    SHOP_OPTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESERVATION404_3",
            "예약 옵션을 찾을 수 없습니다."
    ),

    RESERVATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "RESERVATION404_4",
            "예약을 찾을 수 없습니다."
    ),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "RESERVATION404_5",
            "유저를 찾을 수 없습니다."
    ),

    INVALID_SHOP_OPTION(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_1",
            "해당 샵에서 제공하지 않는 옵션입니다."
    ),

    INACTIVE_SHOP_OPTION(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_2",
            "현재 선택할 수 없는 옵션입니다."
    ),

    OPTION_QUANTITY_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_3",
            "선택 가능한 최대 수량을 초과했습니다."
    ),

    INVALID_RESERVATION_DATE(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_4",
            "예약 가능한 날짜가 아닙니다."
    ),

    INVALID_RESERVATION_TIME(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_5",
            "예약 가능한 시간이 아닙니다."
    ),

    INVALID_GEL_REMOVAL_TYPE(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_6",
            "젤 제거 유형은 젤 네일 상태일 때만 선택할 수 있습니다."
    ),

    INVALID_EXTENSION_REMOVAL_COUNT(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_7",
            "연장 제거 개수는 연장 네일 상태일 때만 입력할 수 있습니다."
    ),

    DUPLICATE_SHOP_OPTION(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_8",
            "동일한 예약 옵션을 중복으로 선택할 수 없습니다."
    ),

    INVALID_OPTION_QUANTITY(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_9",
            "옵션 수량은 1개 이상이어야 합니다."
    ),

    RESERVATION_ALREADY_CONFIRMED(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_10",
            "이미 일정이 확정된 예약입니다."
    ),

    PAST_RESERVATION_DATE(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_11",
            "과거 날짜는 선택할 수 없습니다."
    ),

    N_SELECT_RESERVATION_TIME(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_12",
            "선택할 수 없는 예약 시간입니다."
    ),

    RESERVATION_TIME_CONFLICT(
            HttpStatus.CONFLICT,
            "RESERVATION409_1",
            "이미 예약된 시간과 겹칩니다."
    ),

    INVALID_RESERVATION_DURATION(
            HttpStatus.BAD_REQUEST,
            "RESERVATION400_x",
            "선택한 시간으로는 예약할 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}