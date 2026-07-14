package com.AMOA.server.domain.auth.exception;

import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import com.AMOA.server.global.apiPayload.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
