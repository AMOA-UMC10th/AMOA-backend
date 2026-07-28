package com.amoa.server.domain.user.controller.docs;

import com.amoa.server.domain.card.dto.response.UserCardResDTO;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.user.dto.request.OnboardingSaveReqDTO;
import com.amoa.server.domain.user.dto.request.PhoneSendReqDTO;
import com.amoa.server.domain.user.dto.request.PhoneVerifyReqDTO;
import com.amoa.server.domain.user.dto.request.UserProfileUpdateReqDTO;
import com.amoa.server.domain.user.dto.response.NicknameCheckResDTO;
import com.amoa.server.domain.user.dto.response.OnboardingSaveResDTO;
import com.amoa.server.domain.user.dto.response.PhoneSendResDTO;
import com.amoa.server.domain.user.dto.response.PhoneVerifyResDTO;
import com.amoa.server.domain.user.dto.response.UserProfileResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "User", description = "유저 관련 API")
public interface UserControllerDocs {

    @Operation(
            summary = "회원 탈퇴 API",
            description = "로그인한 회원을 탈퇴 처리합니다. 동일한 카카오 계정으로 다시 로그인하면 재가입이 가능합니다."
    )
    ApiResponse<String> withdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request
    );

    @Operation(
            summary = "찜한 네일샵 목록 조회 API",
            description = "로그인한 사용자가 찜한 네일샵 목록을 조회합니다."
    )
    @SecurityRequirement(name = "JWT TOKEN")
    ApiResponse<SavedShopResDTO.LikedShopListResponse> getLikedShops(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
      
            @Parameter(
                    description = "정렬 기준",
                    example = "RECOMMENDED"
            )
            @RequestParam(
                    defaultValue = "RECOMMENDED"
            )
            SortType sortType,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "6"
            )
            int size
    );

    @Operation(
            summary = "닉네임 중복 확인 API",
            description = "온보딩/설정 화면에서 입력한 닉네임의 형식 유효성과 중복 여부를 확인합니다."
    )
    ApiResponse<NicknameCheckResDTO> checkNickname(
            @RequestParam String nickname
    );

    @Operation(
            summary = "내 정보 조회 API",
            description = "로그인한 사용자의 프로필, 선호 디자인 태그, 관심 지역, 알림 설정을 조회합니다."
    )
    ApiResponse<UserProfileResDTO> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "찜한 아트 목록 조회 API",
            description = "로그인한 사용자가 찜한 아트 목록을 조회합니다."
    )
    @SecurityRequirement(name = "JWT TOKEN")
    ApiResponse<UserCardResDTO.LikedCardListResponse> getLikedCards(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @Parameter(
                    description = """
                            정렬 기준
                            - RECOMMENDED : 추천순 (추후 구현 예정)
                            - POPULAR : 찜 많은 순
                            - PRICE_ASC : 최저 가격 낮은 순
                            - PRICE_DESC : 최고 가격 높은 순
                            - LATEST : 최근 찜한 순
                            """,
                    example = "RECOMMENDED"
            )
            @RequestParam(
                    name = "sortType",
                    defaultValue = "RECOMMENDED"
            )
            SortType sortType,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            int size
    );
    @Operation(
            summary = "디자인 무드 목록 조회 API",
            description = "온보딩/설정 화면에서 선택 가능한 디자인 무드(태그) 목록을 조회합니다."
    )
    ApiResponse<ShopResDTO.DesignTagListResponse> getDesignMoods();

    @Operation(
            summary = "내 정보 통합 수정 API",
            description = """
                로그인한 사용자의 프로필 정보를 부분 수정합니다.
                전달된 필드만 수정되며, null인 필드는 기존 값을 유지합니다.
                디자인 태그와 관심 지역은 빈 리스트를 전달하면 기존 선택값이 모두 삭제됩니다.
                """
    )
    ApiResponse<UserProfileResDTO> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateReqDTO request
    );

    @Operation(
            summary = "온보딩 정보 저장",
            description = "신규 사용자의 온보딩 정보를 한 번에 저장합니다."
    )
    ApiResponse<OnboardingSaveResDTO> saveOnboarding(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Valid
            @RequestBody OnboardingSaveReqDTO request
    );

    @Operation(
            summary = "휴대폰 인증번호 발송 API",
            description = "입력한 전화번호로 6자리 인증번호를 SMS로 발송합니다. 인증번호는 3분간 유효하며, 같은 번호로는 30초 이내 재요청이 제한됩니다."
    )
    ApiResponse<PhoneSendResDTO> sendPhoneVerificationCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PhoneSendReqDTO request
    );

    @Operation(
            summary = "휴대폰 인증번호 확인 API",
            description = "발송된 인증번호와 일치 여부를 확인합니다. 5회 이상 틀리면 인증번호가 폐기되어 재발송이 필요합니다."
    )
    ApiResponse<PhoneVerifyResDTO> verifyPhoneCode(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PhoneVerifyReqDTO request
    );
}
