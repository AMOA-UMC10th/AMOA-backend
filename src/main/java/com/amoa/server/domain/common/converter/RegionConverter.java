package com.amoa.server.domain.common.converter;

import com.amoa.server.domain.common.dto.response.RegionResDTO.RegionInfo;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.user.dto.response.UserProfileResDTO.InterestedRegionDto;
import com.amoa.server.domain.user.entity.mapping.UserRegion;
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

    private static InterestedRegionDto toInterestedRegionDto(
            UserRegion userRegion
    ) {

        Region region = userRegion.getRegion();

        return InterestedRegionDto.builder()
                .legalCode(region.getLegalCode())
                .region1DepthName(region.getFirstDepth())
                .region2DepthName(region.getSecondDepth())
                .region3DepthName(region.getThirdDepth())
                .build();
    }
}
