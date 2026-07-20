package com.amoa.server.domain.common.service.query;

import com.amoa.server.domain.common.converter.RegionConverter;
import com.amoa.server.domain.common.dto.response.RegionResDTO.RegionInfo;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.common.exception.RegionException;
import com.amoa.server.domain.common.exception.code.RegionErrorCode;
import com.amoa.server.domain.common.repository.RegionRepository;
import com.amoa.server.global.kakao.KakaoLocalClient;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionQueryService {

    private final RegionRepository regionRepository;
    private final RegionConverter regionConverter;
    private final KakaoLocalClient kakaoLocalClient;

    // 검색 기반 지역 조회
    public List<RegionInfo> search(String keyword) {

        return regionRepository
                .findByFirstDepthContainingOrSecondDepthContainingOrThirdDepthContaining(
                        keyword,
                        keyword,
                        keyword
                )
                .stream()
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
