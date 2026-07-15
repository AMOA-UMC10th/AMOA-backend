package com.amoa.server.domain.user.controller;

import com.amoa.server.domain.user.dto.response.UserResponseDTO;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.exception.code.UserSuccessCode;
import com.amoa.server.domain.user.service.UserService;
import com.amoa.server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDTO> getUserProfile(
            @PathVariable Long userId
    ) {
        User user = userService.getUser(userId);

        return ApiResponse.onSuccess(UserSuccessCode.USER_FOUND,
                UserResponseDTO.from(user)
        );
    }

}
