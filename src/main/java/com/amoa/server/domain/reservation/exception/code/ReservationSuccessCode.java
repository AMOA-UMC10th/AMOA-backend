package com.amoa.server.domain.reservation.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationSuccessCode implements BaseSuccessCode {

    RESERVATION_OPTIONS_FOUND(
            HttpStatus.OK,
            "RESERVATION200_1",
            "예약 옵션 조회에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}