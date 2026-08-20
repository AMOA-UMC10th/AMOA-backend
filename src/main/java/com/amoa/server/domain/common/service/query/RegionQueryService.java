package com.amoa.server.domain.common.service.query;

import com.amoa.server.domain.common.converter.RegionConverter;
import com.amoa.server.domain.common.dto.response.RegionResDTO.RegionInfo;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.common.exception.RegionException;
import com.amoa.server.domain.common.exception.code.RegionErrorCode;
import com.amoa.server.domain.common.repository.RegionRepository;
import com.amoa.server.global.kakao.KakaoLocalClient;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionQueryService {

    private static final String ALL_REGIONS = "ALL_REGIONS";

    private final RegionRepository regionRepository;
    private final RegionConverter regionConverter;
    private final KakaoLocalClient kakaoLocalClient;
    private final Cache<String, List<Region>> regionCache;

    // 서버 시작 시 전체 Region을 메모리에 적재
    @PostConstruct
    public void loadRegions() {
        regionCache.put(
                ALL_REGIONS,
                regionRepository.findAll()
        );
    }

    private List<Region> getAllRegions() {
        return regionCache.get(
                ALL_REGIONS,
                key -> regionRepository.findAll()
        );
    }

    // 검색 기반 지역 조회
    public List<RegionInfo> search(String keyword) {

        String normalizedKeyword = keyword.trim();

        return getAllRegions().stream()
                .filter(region ->
                        region.getFirstDepth().contains(normalizedKeyword)
                                || region.getSecondDepth().contains(normalizedKeyword)
                                || region.getThirdDepth().contains(normalizedKeyword)
                )
                .map(regionConverter::toRegion)
                .toList();
    }

    // 현재 위치 기반 지역 조회
    public RegionInfo getCurrentRegion(
            BigDecimal latitude,
            BigDecimal longitude
    ) {

        String legalCode =
                kakaoLocalClient.getLegalCode(latitude, longitude);

        Region region = regionRepository.findByLegalCode(legalCode)
                .orElseThrow(() ->
                        new RegionException(RegionErrorCode.REGION_NOT_FOUND)
                );

        return regionConverter.toRegion(region);
    }
}