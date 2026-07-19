package com.amoa.server.domain.reservation.exception;

import com.amoa.server.domain.reservation.exception.code.ReservationErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;

public class ReservationException extends GeneralException {
    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode);
    }
}
