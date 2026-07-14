package com.AMOA.server.domain.auth.service.query;

import com.AMOA.server.domain.auth.exception.code.AuthErrorCode;
import com.AMOA.server.domain.user.dto.respose.KakaoUserInfoResDTO;
import com.AMOA.server.global.apiPayload.exception.GeneralException;
import com.AMOA.server.global.config.KaKaoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiQueryService {
    private final RestClient restClient;
    private final KaKaoProperties kakaoProperties;

    public KakaoUserInfoResDTO getUserInfo(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            log.error("카카오 사용자 정보 조회에 사용할 액세스 토큰이 없습니다.");
            throw new GeneralException(
                    AuthErrorCode.KAKAO_TOKEN_RESPONSE_EMPTY
            );
        }
        try {
            KakaoUserInfoResDTO response = restClient.get()
                    .uri(kakaoProperties.getUserInfoUri())
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + accessToken
                    )
                    .retrieve()
                    .body(KakaoUserInfoResDTO.class);

            if (response == null) {
                log.error("카카오 사용자 정보 응답이 null입니다.");
                throw new GeneralException(AuthErrorCode.KAKAO_RESPONSE_EMPTY);
            }

            return response;

        } catch (RestClientResponseException exception) {
            log.error(
                    "카카오 사용자 정보 조회 실패. status: {}, body: {}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString()
            );

            if (exception.getStatusCode().is4xxClientError()) {
                throw new GeneralException(AuthErrorCode.KAKAO_4XX);
            }

            throw new GeneralException(AuthErrorCode.KAKAO_5XX);
        }catch (ResourceAccessException exception) {

            log.error(
                    "카카오 API 통신 실패",
                    exception
            );

            throw new GeneralException(AuthErrorCode.KAKAO_5XX);

        }
    }
}
