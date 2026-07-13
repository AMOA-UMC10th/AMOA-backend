package com.AMOA.server.domain.shop.exception;

import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class ShopException extends RuntimeException {

  private final BaseErrorCode errorCode;

  public ShopException(BaseErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }
}