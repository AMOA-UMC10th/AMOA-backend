package com.amoa.server.domain.notice.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;

public class NoticeException extends GeneralException {
    public NoticeException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}