package com.amoa.server.domain.shop.exception.code;

import com.amoa.server.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ShopErrorCode implements BaseErrorCode {

    SHOP_INVALID_STATUS(
            HttpStatus.BAD_REQUEST,
            "SHOP400_1",
            "유효하지 않은 상태값입니다."
    ),

    SHOP_INVALID_ADDRESS(
            HttpStatus.BAD_REQUEST,
            "SHOP400_2",
            "주소를 좌표로 변환할 수 없습니다."
    ),

    SHOP_INVALID_DESIGN_TAG(
            HttpStatus.BAD_REQUEST,
            "SHOP400_3",
            "존재하지 않는 디자인태그 ID입니다."
    ),

    SHOP_KEYWORD_EMPTY(
            HttpStatus.BAD_REQUEST,
            "SHOP400_4",
            "검색어를 입력해주세요."
    ),

    REGION_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "SHOP400_5",
            "존재하지 않는 지역입니다."
    ),

    SHOP_INVALID_SORT(
            HttpStatus.BAD_REQUEST,
            "SHOP400_6",
            "지원하지 않는 정렬 조건입니다."
    ),


    SHOP_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_1",
            "존재하지 않는 샵입니다."
    ),

    SHOP_LIKE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_2",
            "찜한 네일샵을 찾을 수 없습니다."
    ),

    KAKAO_SEARCH_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_3",
            "검색 결과가 없습니다."
    ),

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_4",
            "존재하지 않는 유저입니다."
    ),

    SHOP_OPTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_5",
            "샵 옵션을 찾을 수 없습니다."
    ),

    ADDRESS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "SHOP404_6",
            "입력한 주소를 찾을 수 없습니다."
    ),

    SHOP_ALREADY_LIKED(
            HttpStatus.CONFLICT,
            "SHOP409_1",
            "이미 찜한 네일샵입니다."
    ),

    DUPLICATE_SHOP_OPTION(
            HttpStatus.CONFLICT,
            "SHOP409_2",
            "이미 등록된 옵션입니다."
    ),

    SHOP_OPTION_NAME_DUPLICATED(
            HttpStatus.CONFLICT,
            "SHOP409_3",
            "이미 등록된 옵션명입니다."
    ),

    KAKAO_API_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "SHOP500_1",
                    "카카오 로컬 API 호출 중 오류가 발생했습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}