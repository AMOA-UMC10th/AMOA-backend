package com.amoa.server.domain.card.util;

import com.amoa.server.domain.card.exception.CardException;
import com.amoa.server.domain.card.exception.code.CardErrorCode;

// Service가 만든 커서를 받아서 변환
public record CardCursor(
        String[] values
) {

    public static CardCursor from(String cursor) {

        String[] values = cursor.split("_", 3);

        // 정렬값 + id 최소 2개 필요
        if (values.length < 2) {
            throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
        }

        // 마지막 값은 항상 카드 ID
        try {
            Long.parseLong(values[values.length - 1]);
        } catch (NumberFormatException e) {
            throw new CardException(CardErrorCode.CARD_INVALID_CURSOR);
        }

        return new CardCursor(values);
    }

    // 첫 번째 정렬 기준 값
    public String value() {
        return values[0];
    }

    // 두 번째 정렬 기준 값
    // 가격 정렬: maxPrice
    public String secondValue() {
        return values.length == 3
                ? values[1]
                : null;
    }

    // 마지막 카드 ID
    public Long cardId() {
        return Long.parseLong(values[values.length - 1]);
    }
}
