package com.amoa.server.domain.auth.service.command;

import com.amoa.server.domain.auth.dto.response.KakaoTokenResDTO;
import com.amoa.server.domain.auth.exception.AuthException;
import com.amoa.server.domain.auth.exception.code.AuthErrorCode;
import com.amoa.server.global.config.KaKaoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiCommandService {

    private final RestClient restClient;
    private final KaKaoProperties kakaoProperties;

    public String getAccessToken(String authorizationCode) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();

        request.add("grant_type", "authorization_code");
        request.add("client_id", kakaoProperties.getClientId());
        request.add("redirect_uri", kakaoProperties.getRedirectUri());
        request.add("code", authorizationCode);

        if (kakaoProperties.getClientSecret() != null
                && !kakaoProperties.getClientSecret().isBlank()) {
            request.add(
                    "client_secret",
                    kakaoProperties.getClientSecret()
            );
        }

        try {
            KakaoTokenResDTO response = restClient.post()
                    .uri(kakaoProperties.getTokenUri())
                    .contentType(
                            MediaType.APPLICATION_FORM_URLENCODED
                    )
                    .body(request)
                    .retrieve()
                    .body(KakaoTokenResDTO.class);

            if (response == null
                    || response.getAccessToken() == null
                    || response.getAccessToken().isBlank()) {
                throw new AuthException(
                        AuthErrorCode.KAKAO_TOKEN_RESPONSE_EMPTY
                );
            }

            return response.getAccessToken();

        } catch (RestClientResponseException exception) {
            log.error(
                    "카카오 토큰 발급 실패. status={}, body={}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString()
            );

            throw new AuthException(
                    AuthErrorCode.KAKAO_5XX
            );
        }
    }
}