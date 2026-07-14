package com.AMOA.server.domain.card.exception.code;

import com.AMOA.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardErrorCode implements BaseErrorCode {
    CARD_INVALID_DESIGN_TAG(HttpStatus.BAD_REQUEST,
            "CARD400_1",
            "존재하지 않는 디자인 태그ID입니다."),
    CARD_INVALID_PRICE_RANGE(HttpStatus.BAD_REQUEST,
            "CARD400_2",
            "카드 가격 범위 설정이 잘못되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
