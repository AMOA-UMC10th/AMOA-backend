package com.amoa.server.domain.common.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;

public class RegionException extends GeneralException {
    public RegionException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
