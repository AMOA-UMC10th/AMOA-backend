package com.AMOA.server.global.auth;

import com.AMOA.server.domain.user.entity.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(User user) implements UserDetails {
    // 사용자의 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> user.getRole().toString());
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인만을 사용하기 때문에 null을 반환
    }

    @Override
    public String getUsername() {
        return String.valueOf(user.getId());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getIsActive());
    }
}