package com.amoa.server.global.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class KakaoAddressResDTO {

    // 카카오 주소 검색
    public record AddressResponse(
            List<Document> documents
    ) { }

    public record Document(
            Address address,

            @JsonProperty("road_address")
            RoadAddress roadAddress,

            String x,
            String y
    ) { }

    public record Address(
            @JsonProperty("address_name")
            String addressName,

            @JsonProperty("region_1depth_name")
            String region1DepthName,

            @JsonProperty("region_2depth_name")
            String region2DepthName,

            @JsonProperty("region_3depth_name")
            String region3DepthName,

            @JsonProperty("b_code")
            String legalCode
    ) { }

    public record RoadAddress(
            @JsonProperty("address_name")
            String addressName
    ) { }
}
