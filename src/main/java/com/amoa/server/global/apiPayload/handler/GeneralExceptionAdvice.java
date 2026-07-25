package com.amoa.server.global.apiPayload.handler;

import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import com.amoa.server.global.apiPayload.code.GeneralErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionAdvice {

    // 커스텀 예외 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(GeneralException e, HttpServletRequest request) {
        BaseErrorCode code = e.getCode();

        if (code.getHttpStatus().is5xxServerError()) {
            log.error(
                    "서버 예외 발생: method={}, uri={}, code={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    code.getCode(),
                    e
            );
        } else {
            log.warn(
                    "클라이언트 오류 발생: method={}, uri={}, code={}, message={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    code.getCode(),
                    code.getMessage()
            );
        }

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(code));
    }

    // @Valid에서 검증 오류가 발생한 예외에 대한 핸들러
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(BindException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        String message = (fieldError != null) ? fieldError.getDefaultMessage() : "검증 오류가 발생했습니다.";
        String fieldName = (fieldError != null) ? fieldError.getField() : "알 수 없는 필드";

        log.warn(
                "검증 오류 발생: method={}, uri={}, field={}, message={}",
                request.getMethod(),
                request.getRequestURI(),
                fieldName,
                message
        );

        // 400 Bad Request 로 응답
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(
                        GeneralErrorCode.BAD_REQUEST,
                        String.format("[%s] %s", fieldName, message)
                ));
    }

    // 그 외의 정의되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;

        log.error(
                "예기치 않은 서버 오류 발생: method={}, uri={}",
                request.getMethod(),
                request.getRequestURI(),
                e
        );

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.onFailure(
                        code,
                        "서버 내부 오류가 발생하였습니다."
                ));
    }
}
