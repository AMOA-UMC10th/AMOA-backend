package com.AMOA.server.domain.auth.converter;

import com.AMOA.server.domain.auth.dto.response.AuthResDTO;
import com.AMOA.server.domain.user.entity.User;

public class AuthConverter {
    public static AuthResDTO.LoginResultDTO toExistingMemberDTO(
            User user,
            String accessToken,
            String refreshToken
    ) {
        return AuthResDTO.LoginResultDTO.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
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
                .nickname(user.getNickname())
                .isNewUser(true)
                .role(user.getRole())
                .build();
    }
}
