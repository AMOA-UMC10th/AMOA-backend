package com.amoa.server.domain.auth.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
