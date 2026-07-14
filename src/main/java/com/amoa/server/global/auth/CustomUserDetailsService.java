package com.amoa.server.global.auth;

import com.amoa.server.domain.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @NonNull
    @Override
    public UserDetails loadUserByUsername(@NonNull String userId) { // jwt에서 추출한 userId를 문자열 형태로 받음
        return userRepository.findById(Long.parseLong(userId))
                .map(CustomUserDetails::new) // 조회된 User 객체로 CustomUserDetails 객체 생성
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다. ID: " + userId));
    }
}
