package com.amoa.server.global.kakao;

import com.amoa.server.domain.common.exception.RegionException;
import com.amoa.server.domain.common.exception.code.RegionErrorCode;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.global.kakao.dto.response.KakaoRegionResDTO;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://dapi.kakao.com")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .responseTimeout(Duration.ofSeconds(5))  // 응답 타임아웃 5초
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)  // 연결 타임아웃 3초
            ))
            .build();

    // 샵 이름으로 검색 (자동완성용)
    @SuppressWarnings("unchecked")
    public ShopResDTO.KakaoSearchResponse searchByKeyword(String keyword) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/keyword.json")
                            .queryParam("query", keyword)
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            List<Map<String, Object>> documents =
                    (List<Map<String, Object>>) response.get("documents");

            if (documents == null || documents.isEmpty()) {
                return null;
            }

            // 첫 번째 결과만 반환
            Map<String, Object> place = documents.get(0);

            return ShopResDTO.KakaoSearchResponse.builder()
                    .placeName((String) place.get("place_name"))
                    .address((String) place.get("road_address_name"))
                    .phone((String) place.get("phone"))
                    .build();

        } catch (Exception e) {
            log.error("카카오 키워드 검색 중 오류 발생: {}", e.getMessage(), e);
            throw new ShopException(ShopErrorCode.KAKAO_API_ERROR, e);
        }
    }

    // 주소 → 좌표 변환 (샵 등록 시)
    @SuppressWarnings("unchecked")
    public BigDecimal[] getCoordinates(String address) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                    })
                    .block();

            List<Map<String, Object>> documents =
                    (List<Map<String, Object>>) response.get("documents");

            if (documents == null || documents.isEmpty()) {
                return null;
            }

            Map<String, Object> location = documents.get(0);
            BigDecimal latitude = new BigDecimal((String) location.get("y"));
            BigDecimal longitude = new BigDecimal((String) location.get("x"));

            return new BigDecimal[]{latitude, longitude};

        } catch (Exception e) {
            log.error("카카오 로컬 API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new ShopException(ShopErrorCode.KAKAO_API_ERROR, e);
        }
    }


    // 카카오 좌표 -> 법정동 코드 조회
    public String getLegalCode(
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        try {

            KakaoRegionResDTO.Response response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/geo/coord2regioncode.json")
                            .queryParam("x", longitude)
                            .queryParam("y", latitude)
                            .build())
                    .header("Authorization", "KakaoAK " + kakaoApiKey)
                    .retrieve()
                    .bodyToMono(KakaoRegionResDTO.Response.class)
                    .block();

            if (response == null
                    || response.documents() == null
                    || response.documents().isEmpty()) {
                throw new RegionException(
                        RegionErrorCode.REGION_NOT_FOUND
                );
            }

            return response.documents().stream()
                    .filter(document -> "B".equals(document.regionType()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RegionException(
                                    RegionErrorCode.REGION_NOT_FOUND
                            ))
                    .code();

        } catch (RegionException e) {
            throw e;

        } catch (Exception e) {
            log.error("카카오 좌표→법정동 코드 조회 실패", e);

            throw new RegionException(
                    RegionErrorCode.KAKAO_API_ERROR
            );
        }
    }
}