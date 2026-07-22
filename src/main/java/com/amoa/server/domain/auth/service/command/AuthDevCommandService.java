package com.amoa.server.domain.auth.service.command;

import com.amoa.server.domain.auth.converter.AuthDevConverter;
import com.amoa.server.domain.auth.dto.request.AuthDevReqDTO;
import com.amoa.server.domain.auth.dto.response.AuthDevResDTO;
import com.amoa.server.domain.auth.exception.AuthException;
import com.amoa.server.domain.auth.exception.code.AuthErrorCode;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.enums.Role;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserRepository;
import com.amoa.server.global.config.JwtProperties;
import com.amoa.server.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthDevCommandService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    public AuthDevResDTO.DevTokenResponse issueDevToken(
            AuthDevReqDTO.DevTokenRequest request
    ) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        new UserException(UserErrorCode.USER_NOT_FOUND)
                );

        validateDevUser(user);

        String accessToken = jwtUtil.createDevToken(user.getId());

        long expiresIn =
                jwtProperties.getDevToken().getExpirationTime() / 1000;

        return AuthDevConverter.toDevTokenResponse(
                accessToken,
                expiresIn
        );
    }

    private void validateDevUser(User user) {
        if (user.getRole() != Role.USER) {
            throw new AuthException(
                    AuthErrorCode.DEV_USER_ROLE_INVALID
            );
        }
    }
}
