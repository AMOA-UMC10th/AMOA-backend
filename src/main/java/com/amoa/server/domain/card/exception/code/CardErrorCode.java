package com.amoa.server.domain.card.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardErrorCode implements BaseErrorCode {

    CARD_INVALID_DESIGN_TAG(
            HttpStatus.BAD_REQUEST,
            "CARD400_1",
            "존재하지 않는 디자인 태그ID입니다."
    ),

    CARD_INVALID_PRICE_RANGE(
            HttpStatus.BAD_REQUEST,
            "CARD400_2",
            "카드 가격 범위 설정이 잘못되었습니다."
    ),

    CARD_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CARD404_1",
            "존재하지 않는 카드ID 입니다."
    ),

    CARD_LIKE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CARD404_2",
            "찜한 카드를 찾을 수 없습니다."
    ),

    CARD_ALREADY_LIKED(
            HttpStatus.CONFLICT,
            "CARD409_1",
            "이미 찜한 카드입니다."
    ),

    CARD_INVALID_CURSOR(
            HttpStatus.BAD_REQUEST,
            "CARD400_3",
            "잘못된 커서 형식입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
