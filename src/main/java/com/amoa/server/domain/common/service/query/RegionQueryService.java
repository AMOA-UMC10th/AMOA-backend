package com.amoa.server.domain.common.service.query;

import com.amoa.server.domain.common.converter.RegionConverter;
import com.amoa.server.domain.common.dto.response.RegionResDTO.SearchRegion;
import com.amoa.server.global.kakao.KakaoLocalClient;
import com.amoa.server.global.kakao.dto.response.KakaoRegionResDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionQueryService {

    private final KakaoLocalClient kakaoLocalClient;
    private final RegionConverter regionConverter;

    // 검색 기반 지역 조회
    public List<SearchRegion> search(String keyword) {

        KakaoRegionResDTO.Response response =
                kakaoLocalClient.searchRegionByKeyword(keyword);

        if (response == null || response.documents() == null) {
            return List.of();
        }

        return response.documents()
                .stream()
                .map(regionConverter::toSearchRegion)
                .toList();
    }
}
