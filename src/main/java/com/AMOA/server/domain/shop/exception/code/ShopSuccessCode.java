package com.AMOA.server.domain.shop.exception.code;

import com.AMOA.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ShopSuccessCode implements BaseSuccessCode {

    SHOP_CREATED(HttpStatus.CREATED, "SHOP201", "샵이 등록되었습니다."),
    SHOP_FOUND(HttpStatus.OK, "SHOP200", "샵 상세 조회에 성공했습니다."),
    SHOP_LIST_FOUND(HttpStatus.OK, "SHOP200", "샵 목록 조회에 성공했습니다."),
    SHOP_UPDATED(HttpStatus.OK, "SHOP200", "샵이 수정되었습니다."),
    SHOP_DELETED(HttpStatus.OK, "SHOP200", "샵이 삭제되었습니다."),
    DESIGNTAG_LIST_FOUND(HttpStatus.OK, "SHOP200", "디자인태그 목록 조회에 성공했습니다."),
    KAKAO_SEARCH_FOUND(HttpStatus.OK, "SHOP200", "카카오 로컬 API 검색에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}