package com.AMOA.server.global.kakao;

import com.AMOA.server.domain.shop.dto.Response.ShopResDTO;
import com.AMOA.server.domain.shop.exception.ShopException;
import com.AMOA.server.domain.shop.exception.code.ShopErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://dapi.kakao.com")
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
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
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
            throw new ShopException(ShopErrorCode.KAKAO_API_ERROR);
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
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
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
            throw new ShopException(ShopErrorCode.KAKAO_API_ERROR);
        }
    }
}