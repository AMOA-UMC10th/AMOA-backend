package com.amoa.server.global.sms;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SolapiScheduleClient {

    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.solapi.com")
            .build();

    // 예약발송 취소 (실패해도 예약 취소 흐름은 막지 않음 - 호출부에서 예외 삼킴)
    public void cancelSchedule(String groupId) {
        webClient.delete()
                .uri("/messages/v4/groups/{groupId}/schedule", groupId)
                .header("Authorization", generateAuthHeader())
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private String generateAuthHeader() {
        try {
            String date = Instant.now().toString();
            String salt = UUID.randomUUID().toString().replace("-", "");

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String signature = HexFormat.of()
                    .formatHex(mac.doFinal((date + salt).getBytes(StandardCharsets.UTF_8)));

            return "HMAC-SHA256 apikey=" + apiKey + ", date=" + date + ", salt=" + salt + ", signature=" + signature;
        } catch (Exception e) {
            throw new IllegalStateException("솔라피 인증 헤더 생성 실패", e);
        }
    }
}