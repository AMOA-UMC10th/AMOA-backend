package com.AMOA.server.domain.shop.exception;

import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class ShopException extends RuntimeException {

  private final BaseErrorCode errorCode;

  public ShopException(BaseErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}