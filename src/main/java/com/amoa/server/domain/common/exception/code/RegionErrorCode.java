package com.amoa.server.domain.common.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RegionErrorCode implements BaseErrorCode {

    REGION_NOT_FOUND(HttpStatus.NOT_FOUND,
            "REGION404_1",
            "조회되지 않는 지역입니다."),
    KAKAO_API_ERROR(HttpStatus.BAD_GATEWAY,
            "REGION502_1",
            "카카오 API 호출 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
