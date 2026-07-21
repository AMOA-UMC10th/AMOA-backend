package com.amoa.server.domain.card.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardSuccessCode implements BaseSuccessCode {
    CARD_CREATED(
            HttpStatus.CREATED,
            "CARD201_1",
            "카드가 등록되었습니다."
    ),

    CARD_LIKED(
            HttpStatus.OK,
            "CARD200_1",
            "카드 찜 등록에 성공했습니다."
    ),

    CARD_UNLIKED(
            HttpStatus.OK,
            "CARD200_2",
            "카드 찜 취소에 성공했습니다."
    ),

    CARD_KAKAO_CHANNEL_FOUND(
            HttpStatus.OK,
            "CARD200_3",
            "카카오톡 채널 URL을 성공적으로 조회했습니다."
    ),
    CARD_FOUND(HttpStatus.OK,
            "CARD200_4",
            "카드를 성공적으로 조회했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
