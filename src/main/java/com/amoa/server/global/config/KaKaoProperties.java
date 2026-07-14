package com.amoa.server.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KaKaoProperties {
    private String clientId; // 카카오 REST API 키를 저장하는 필드
    private String clientSecret; // 보안 강화를 위해 사용하는 비밀키(선택)
    private String redirectUri; // 인증 코드를 전달받을 API 주소
    private String authorizationUri; // 카카오 로그인 페이지 주소
    private String tokenUri; // 인가 코드를 토큰으로 교환할 때 사용하는 주소
    private String userInfoUri; // 액세스 토큰으로 사용자 정보를 가져올 때 사용하는 주소
}
