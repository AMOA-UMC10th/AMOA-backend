package com.amoa.server.domain.common.dto.response;

public class RegionResDTO {

    // 검색 기반 지역 조회
    public record SearchRegion(
            String regionName
    ) {
    }
}
