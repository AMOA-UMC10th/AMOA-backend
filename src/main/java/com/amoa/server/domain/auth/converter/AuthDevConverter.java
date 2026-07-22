package com.amoa.server.domain.auth.converter;

import com.amoa.server.domain.auth.dto.response.AuthDevResDTO;

public class AuthDevConverter {

    public static AuthDevResDTO.DevTokenResponse toDevTokenResponse(
            String accessToken,
            long expiresIn
    ) {
        return new AuthDevResDTO.DevTokenResponse(
                accessToken,
                expiresIn
        );
    }
}
