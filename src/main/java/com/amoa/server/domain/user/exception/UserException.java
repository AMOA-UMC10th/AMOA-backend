package com.AMOA.server.domain.user.exception;

import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import com.AMOA.server.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
