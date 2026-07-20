package com.amoa.server.domain.common.converter;

import com.amoa.server.domain.common.dto.response.RegionResDTO.RegionInfo;
import com.amoa.server.domain.common.entity.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionConverter {

    public RegionInfo toRegion(
            Region region
    ) {
        return new RegionInfo(
                region.getId(),
                region.getFirstDepth(),
                region.getSecondDepth(),
                region.getThirdDepth()
        );
    }
}
