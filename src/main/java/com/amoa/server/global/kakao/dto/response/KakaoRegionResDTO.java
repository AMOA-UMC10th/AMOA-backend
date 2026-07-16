package com.amoa.server.global.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class KakaoRegionResDTO {

    public record Response(
            List<Document> documents
    ) {

        public record Document(
                Address address
        ) {
        }

        public record Address(

                @JsonProperty("region_1depth_name")
                String city,

                @JsonProperty("region_2depth_name")
                String district,

                @JsonProperty("region_3depth_name")
                String dong
        ) {
        }
    }
}
