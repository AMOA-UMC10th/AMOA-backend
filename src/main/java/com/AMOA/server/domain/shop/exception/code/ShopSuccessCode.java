package com.amoa.server.domain.shop.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ShopSuccessCode implements BaseSuccessCode {

    SHOP_LIKED(
            HttpStatus.OK,
            "SHOP200_1",
            "네일샵 찜 등록에 성공했습니다."
    ),

    SHOP_UNLIKED(
            HttpStatus.OK,
            "SHOP200_2",
            "네일샵 찜 취소에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
