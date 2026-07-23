package com.amoa.server.domain.term.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TermErrorCode implements BaseErrorCode {

    TERM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "TERM404_1",
            "존재하지 않는 이용약관입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
