package com.amoa.server.domain.auth.controller;

import com.amoa.server.domain.auth.controller.docs.AuthDevControllerDocs;
import com.amoa.server.domain.auth.dto.request.AuthDevReqDTO;
import com.amoa.server.domain.auth.dto.response.AuthDevResDTO;
import com.amoa.server.domain.auth.exception.code.AuthSuccessCode;
import com.amoa.server.domain.auth.service.command.AuthDevCommandService;
import com.amoa.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("dev")
@RestController
@RequestMapping("/api/v1/dev/auth")
@RequiredArgsConstructor
public class AuthDevController implements AuthDevControllerDocs {

    private final AuthDevCommandService authDevCommandService;

    @Override
    @PostMapping("/token")
    public ApiResponse<AuthDevResDTO.DevTokenResponse> issueDevToken(
            @Valid @RequestBody AuthDevReqDTO.DevTokenRequest request
    ) {
        return ApiResponse.onSuccess(
                AuthSuccessCode.AUTH_DEV_TOKEN_OK,
                authDevCommandService.issueDevToken(request)
        );
    }
}
