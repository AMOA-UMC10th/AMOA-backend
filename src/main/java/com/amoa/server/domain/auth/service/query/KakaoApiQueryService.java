package com.amoa.server.domain.auth.service.query;

import com.amoa.server.domain.auth.exception.code.AuthErrorCode;
import com.amoa.server.domain.user.dto.respose.KakaoUserInfoResDTO;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import com.amoa.server.global.config.KaKaoProperties;
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
