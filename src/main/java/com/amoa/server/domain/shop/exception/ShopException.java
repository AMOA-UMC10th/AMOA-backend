package com.amoa.server.domain.shop.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class ShopException extends RuntimeException {

  private final BaseErrorCode errorCode;

  // 인수 1개
  public ShopException(BaseErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  // 인수 2개
  public ShopException(BaseErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }
}