package com.amoa.server.domain.common.converter;

import com.amoa.server.domain.common.dto.response.RegionResDTO.SearchRegion;
import com.amoa.server.global.kakao.dto.response.KakaoRegionResDTO;
import org.springframework.stereotype.Component;

@Component
public class RegionConverter {

    public SearchRegion toSearchRegion(
            KakaoRegionResDTO.Response.Document document
    ) {

        KakaoRegionResDTO.Response.Address address =
                document.address();

        String regionName = String.join(" ",
                address.city(),
                address.district(),
                address.dong()
        );

        return new SearchRegion(regionName);
    }
}
