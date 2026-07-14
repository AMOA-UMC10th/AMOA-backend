package com.amoa.server.domain.user.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
