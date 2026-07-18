package com.amoa.server.domain.shop.exception.code;

import com.amoa.server.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ShopSuccessCode implements BaseSuccessCode {

    SHOP_CREATED(HttpStatus.CREATED, "SHOP201_1", "샵이 등록되었습니다."),
    SHOP_FOUND(HttpStatus.OK, "SHOP200_1", "샵 상세 조회에 성공했습니다."),
    SHOP_LIST_FOUND(HttpStatus.OK, "SHOP200_2", "샵 목록 조회에 성공했습니다."),
    SHOP_UPDATED(HttpStatus.OK, "SHOP200_3", "샵이 수정되었습니다."),
    SHOP_DELETED(HttpStatus.OK, "SHOP200_4", "샵이 삭제되었습니다."),
    SHOP_LIKED(HttpStatus.OK, "SHOP200_5", "네일샵 찜 등록에 성공했습니다."),
    SHOP_UNLIKED(HttpStatus.OK, "SHOP200_6", "네일샵 찜 취소에 성공했습니다."),
    DESIGNTAG_LIST_FOUND(HttpStatus.OK, "SHOP200_7", "디자인태그 목록 조회에 성공했습니다."),
    KAKAO_SEARCH_FOUND(HttpStatus.OK, "SHOP200_8", "카카오 로컬 API 검색에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}