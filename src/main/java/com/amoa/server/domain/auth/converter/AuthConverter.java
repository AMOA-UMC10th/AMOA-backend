package com.amoa.server.domain.auth.converter;

import com.amoa.server.domain.auth.dto.response.AuthResDTO;
import com.amoa.server.domain.user.entity.User;

public class AuthConverter {
    public static AuthResDTO.LoginResultDTO toExistingMemberDTO(
            User user,
            String accessToken,
            String refreshToken
    ) {
        return AuthResDTO.LoginResultDTO.builder()
                .email(user.getEmail())
                .userName(user.getUserName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(false)
                .role(user.getRole())
                .build();
    }

    public static AuthResDTO.LoginResultDTO toNewMemberDTO(
            User user,
            String tempToken
    ) {
        return AuthResDTO.LoginResultDTO.builder()
                .tempToken(tempToken)
                .email(user.getEmail())
                .userName(user.getUserName())
                .isNewUser(true)
                .role(user.getRole())
                .build();
    }
}
