package com.amoa.server.domain.common.service.query;

import com.amoa.server.domain.common.converter.RegionConverter;
import com.amoa.server.domain.common.dto.response.RegionResDTO.SearchRegion;
import com.amoa.server.domain.common.repository.RegionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionQueryService {

    private final RegionRepository regionRepository;
    private final RegionConverter regionConverter;

    // 검색 기반 지역 조회
    public List<SearchRegion> search(String keyword) {

        return regionRepository
                .findByFirstDepthContainingOrSecondDepthContainingOrThirdDepthContaining(
                        keyword,
                        keyword,
                        keyword
                )
                .stream()
                .map(regionConverter::toSearchRegion)
                .toList();
    }
}
