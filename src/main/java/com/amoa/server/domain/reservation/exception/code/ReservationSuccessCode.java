package com.amoa.server.domain.reservation.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationSuccessCode implements BaseSuccessCode {

    RESERVATION_CREATED(
            HttpStatus.OK,
            "RESERVATION200_1",
            "예약이 완료되었습니다."
    ),

    RESERVATION_FOUND(
            HttpStatus.OK,
            "RESERVATION200_2",
            "예약 조회에 성공했습니다."
    ),

    RESERVATION_OPTIONS_FOUND(
            HttpStatus.OK,
            "RESERVATION200_3",
            "예약 가능한 옵션 조회에 성공했습니다."
    ),

    RESERVATION_SCHEDULE_CONFIRMED(
            HttpStatus.OK,
            "RESERVATION200_4",
            "예약 일정 확정에 성공했습니다."
    ),

    RESERVATION_AVAILABLE_TIMES_FOUND(
            HttpStatus.OK,
            "RESERVATION200_5",
            "예약 가능 시간 조회에 성공했습니다."
    ),

    RESERVATION_DETAIL_SUCCESS(
            HttpStatus.OK,
            "RESERVATION200_6",
            "예약 상세 조회에 성공했습니다."
    ),

    RESERVATION_LIST_OK(
            HttpStatus.OK,
            "RESERVATION200_7",
            "예약 목록 조회에 성공했습니다."
    ),

    RESERVATION_CANCEL_OK(
            HttpStatus.OK,
            "RESERVATION200_8",
            "예약 취소에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}