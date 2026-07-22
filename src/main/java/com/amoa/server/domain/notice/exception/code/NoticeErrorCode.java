package com.amoa.server.domain.notice.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NoticeErrorCode implements BaseErrorCode {

    NOTICE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
        "NOTICE404_1",
                "존재하지 않는 공지사항입니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
