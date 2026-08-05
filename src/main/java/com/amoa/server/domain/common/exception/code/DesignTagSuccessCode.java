package com.amoa.server.domain.common.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DesignTagSuccessCode implements BaseSuccessCode {

    DESIGN_TAG_LIST_FOUND(
            HttpStatus.OK,
            "DesignTag200_1",
            "디자인태그 목록 조회에 성공했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}