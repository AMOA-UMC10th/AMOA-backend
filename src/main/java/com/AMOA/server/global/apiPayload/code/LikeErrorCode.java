package com.AMOA.server.global.apiPayload.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeErrorCode implements BaseErrorCode{

    SHOP_ALREADY_LIKED(
            HttpStatus.BAD_REQUEST,
            "LIKE400_1",
            "이미 찜한 네일숍입니다."
    ),

    SHOP_LIKE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "LIKE404_1",
            "찜한 내역을 찾을 수 없습니다."
    ),

    CARD_ALREADY_LIKED(
            HttpStatus.BAD_REQUEST,
            "LIKE400_2",
            "이미 찜한 네일아트입니다."
    ),

    CARD_LIKE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "LIKE404_2",
            "찜한 내역을 찾을 수 없습니다."
    );




    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
