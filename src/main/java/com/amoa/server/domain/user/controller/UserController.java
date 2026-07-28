package com.amoa.server.domain.user.controller;

import com.amoa.server.domain.auth.exception.AuthException;
import com.amoa.server.domain.auth.exception.code.AuthErrorCode;
import com.amoa.server.domain.card.dto.response.UserCardResDTO;
import com.amoa.server.domain.card.service.query.UserCardQueryService;
import com.amoa.server.domain.common.enums.SortType;
import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.service.query.SavedShopQueryService;
import com.amoa.server.domain.user.controller.docs.UserControllerDocs;
import com.amoa.server.domain.user.dto.request.OnboardingSaveReqDTO;
import com.amoa.server.domain.user.dto.request.UserProfileUpdateReqDTO;
import com.amoa.server.domain.user.dto.response.NicknameCheckResDTO;
import com.amoa.server.domain.user.dto.response.OnboardingSaveResDTO;
import com.amoa.server.domain.user.dto.response.UserProfileResDTO;
import com.amoa.server.domain.user.exception.code.UserSuccessCode;
import com.amoa.server.domain.user.service.command.OnboardingCommandService;
import com.amoa.server.domain.user.service.command.UserCommandService;
import com.amoa.server.domain.user.service.command.UserProfileCommandService;
import com.amoa.server.domain.user.service.query.UserQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserCommandService userCommandService;
    private final UserProfileCommandService userProfileCommandService;
    private final SavedShopQueryService savedShopQueryService;
    private final UserCardQueryService userCardQueryService;
    private final UserQueryService userQueryService;
    private final OnboardingCommandService onboardingCommandService;

    @Override
    @DeleteMapping("/me/withdraw")
    public ApiResponse<String> withdraw(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request
    ){
        String accessToken = resolveAccessToken(request);

        userCommandService.withdrawalUser(
                customUserDetails.user().getId(),
                accessToken
        );
        return ApiResponse.onSuccess(UserSuccessCode.USER_WITHDRAW_SUCCESS, "회원 탈퇴가 완료되었습니다.");
    }

    @Override
    @GetMapping("/me/liked-shops")
    public ApiResponse<SavedShopResDTO.LikedShopListResponse> getLikedShops(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @RequestParam(
                name = "sortType",
                defaultValue = "RECOMMENDED")
            SortType sortType,

            @RequestParam(
                    defaultValue = "0"
            )
            @Min(0)
            int page,

            @RequestParam(
                    defaultValue = "6"
            )
            @Min(1)
            @Max(50)
            int size
    ){

        Pageable pageable = PageRequest.of(page, size);

        return ApiResponse.onSuccess(
                UserSuccessCode.USER_LIKED_SHOPS_SUCCESS,
                savedShopQueryService.getLikedShops(
                        customUserDetails.user(),
                        sortType,
                        pageable
                )
        );
    }

    @Override
    @GetMapping("/me/liked-cards")
    public ApiResponse<UserCardResDTO.LikedCardListResponse> getLikedCards(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,

            @RequestParam(
                    name = "sortType",
                    defaultValue = "RECOMMENDED"
            )
            SortType sortType,

            @RequestParam(
                    defaultValue = "0"
            )
            @Min(0)
            int page,

            @RequestParam(
                    defaultValue = "20"
            )
            @Min(1)
            @Max(100)
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ApiResponse.onSuccess(
                UserSuccessCode.USER_LIKED_CARDS_SUCCESS,
                userCardQueryService.getLikedCards(
                        customUserDetails.user(),
                        sortType,
                        pageable
                )
        );
    }


    private String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        String accessToken = authorization.substring(7).trim();

        if (accessToken.isBlank()) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        return accessToken;
    }

    @Override
    @GetMapping("/nickname/check")
    public ApiResponse<NicknameCheckResDTO> checkNickname(
            @RequestParam String nickname
    ) {
        return ApiResponse.onSuccess(
                UserSuccessCode.NICKNAME_CHECK_SUCCESS,
                userQueryService.checkNickname(nickname)
        );
    }

    @Override
    @GetMapping("/me/profile")
    public ApiResponse<UserProfileResDTO> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(
                UserSuccessCode.USER_PROFILE_GET_OK,
                userQueryService.getMyProfile(userDetails.user().getId())
        );
    }

    @Override
    @GetMapping("/design-moods")
    public ApiResponse<ShopResDTO.DesignTagListResponse> getDesignMoods() {
        return ApiResponse.onSuccess(
                UserSuccessCode.DESIGN_MOOD_LIST_FOUND,
                userQueryService.getDesignMoods()
        );
    }

    @Override
    @PatchMapping("/me/profile")
    public ApiResponse<UserProfileResDTO> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateReqDTO request
    ) {
        Long userId = userDetails.user().getId();

        userProfileCommandService.updateUserProfile(userId, request);

        UserProfileResDTO response =
                userQueryService.getMyProfile(userId);

        return ApiResponse.onSuccess(
                UserSuccessCode.USER_PROFILE_UPDATE_OK,
                response
        );
    }

    @Override
    @PostMapping("/onboarding")
    public ApiResponse<OnboardingSaveResDTO> saveOnboarding(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OnboardingSaveReqDTO request
    ) {
        Long userId = userDetails.user().getId();

        return ApiResponse.onSuccess(
                UserSuccessCode.ONBOARDING_COMPLETE_OK,
                onboardingCommandService.saveOnboarding(
                        userId,
                        request
                )
        );
    }
}
