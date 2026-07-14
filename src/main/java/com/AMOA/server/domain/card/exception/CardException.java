package com.AMOA.server.domain.card.exception;

import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import com.AMOA.server.global.apiPayload.exception.GeneralException;

public class CardException extends GeneralException {
    public CardException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
