package com.amoa.server.domain.term.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;

public class TermException extends GeneralException {
    public TermException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}