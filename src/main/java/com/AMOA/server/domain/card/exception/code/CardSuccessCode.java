package com.AMOA.server.domain.card.exception.code;

import com.AMOA.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardSuccessCode implements BaseSuccessCode {
    CARD_CREATED(HttpStatus.CREATED,
            "CARD201_1",
            "카드가 등록되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
