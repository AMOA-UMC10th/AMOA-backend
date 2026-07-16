package com.amoa.server.domain.common.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RegionSuccessCode implements BaseSuccessCode {
    REGION_FOUND(HttpStatus.OK,
            "REGION200_1",
            "지역을 성공적으로 조회했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
