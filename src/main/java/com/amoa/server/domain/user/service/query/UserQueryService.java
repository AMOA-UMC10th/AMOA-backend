package com.amoa.server.domain.user.service.query;

import com.amoa.server.domain.user.converter.UserConverter;
import com.amoa.server.domain.user.dto.response.NicknameCheckResDTO;
import com.amoa.server.domain.user.exception.UserException;
import com.amoa.server.domain.user.exception.code.UserErrorCode;
import com.amoa.server.domain.user.repository.UserRepository;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");

    private final UserRepository userRepository;

    public NicknameCheckResDTO checkNickname(String nickname) {
        if (nickname == null || !NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new UserException(UserErrorCode.NICKNAME_INVALID_FORMAT);
        }

        boolean available = !userRepository.existsByNickname(nickname);
        return UserConverter.toNicknameCheckResDTO(nickname, available);
    }
}