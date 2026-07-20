package com.amoa.server.domain.shop.exception;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class ShopException extends GeneralException {

  // 인수 1개
  public ShopException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}