package com.amoa.server.domain.user.controller;

import com.amoa.server.domain.auth.exception.AuthException;
import com.amoa.server.domain.auth.exception.code.AuthErrorCode;
import com.amoa.server.domain.user.controller.docs.UserControllerDocs;
import com.amoa.server.domain.user.exception.code.UserSuccessCode;
import com.amoa.server.domain.user.service.command.UserCommandService;
import com.amoa.server.global.apiPayload.ApiResponse;
import com.amoa.server.global.auth.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserCommandService userCommandService;

    @Override
    @DeleteMapping("/me")
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
}
