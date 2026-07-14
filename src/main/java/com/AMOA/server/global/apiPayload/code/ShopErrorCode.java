package com.AMOA.server.global.apiPayload.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ShopErrorCode implements BaseErrorCode {

    SHOP_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_1",
            "네일 샵을 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
