package com.AMOA.server.global.apiPayload.exception;

import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

  private final BaseErrorCode code;

  public GeneralException(BaseErrorCode code) {
    super(code.getMessage());
    this.code = code;
  }
}
