package com.amoa.server.domain.shop.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ShopErrorCode implements BaseErrorCode {

    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "SHOP404_1", "존재하지 않는 샵입니다."),
    SHOP_INVALID_STATUS(HttpStatus.BAD_REQUEST, "SHOP400_1", "유효하지 않은 상태값입니다."),
    SHOP_INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "SHOP400_2", "주소를 좌표로 변환할 수 없습니다."),
    SHOP_INVALID_DESIGNTAG(HttpStatus.BAD_REQUEST, "SHOP400_3", "존재하지 않는 디자인태그 ID입니다."),
    SHOP_KEYWORD_EMPTY(HttpStatus.BAD_REQUEST, "SHOP400_4", "검색어를 입력해주세요."),
    SHOP_ALREADY_LIKED(HttpStatus.CONFLICT, "SHOP409_1", "이미 찜한 네일샵입니다."),
    SHOP_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "SHOP404_2", "찜한 네일샵을 찾을 수 없습니다."),
    KAKAO_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SHOP500_1", "카카오 로컬 API 호출 중 오류가 발생했습니다."),
    KAKAO_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "SHOP404_3", "검색 결과가 없습니다."),
    REGION_NOT_FOUND(HttpStatus.BAD_REQUEST, "SHOP400_5", "존재하지 않는 지역입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}