package com.amoa.server.domain.card.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;

public class CardException extends GeneralException {
    public CardException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
