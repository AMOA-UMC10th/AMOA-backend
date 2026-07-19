package com.amoa.server.global.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class KakaoRegionResDTO {

    // 카카오 좌표 -> 지역 코드 변환
    public record Response(
            List<Document> documents
    ) {

        public record Document(

                String code,

                @JsonProperty("region_type")
                String regionType

        ) {
        }
    }
}
