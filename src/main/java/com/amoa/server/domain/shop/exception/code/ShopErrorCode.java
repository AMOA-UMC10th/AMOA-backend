package com.amoa.server.domain.shop.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
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
    ),

    SHOP_ALREADY_LIKED(
            HttpStatus.CONFLICT,
            "SHOP409_1",
            "이미 찜한 네일샵입니다."
    ),

    SHOP_LIKE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_2",
            "찜한 네일샵을 찾을 수 없습니다."
    );


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
