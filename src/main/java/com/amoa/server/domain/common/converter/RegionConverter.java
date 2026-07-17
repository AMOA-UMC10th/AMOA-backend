package com.amoa.server.domain.common.converter;

import com.amoa.server.domain.common.dto.response.RegionResDTO;
import com.amoa.server.domain.common.entity.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionConverter {

    // 검색 기반 지역 조회
    public RegionResDTO.SearchRegion toSearchRegion(
            Region region
    ) {
        return new RegionResDTO.SearchRegion(
                region.getId(),
                region.getFirstDepth(),
                region.getSecondDepth(),
                region.getThirdDepth()
        );
    }
}
