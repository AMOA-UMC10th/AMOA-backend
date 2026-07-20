package com.amoa.server.domain.common.controller;

import com.amoa.server.domain.common.controller.docs.RegionControllerDocs;
import com.amoa.server.domain.common.dto.response.RegionResDTO.RegionInfo;
import com.amoa.server.domain.common.exception.code.RegionSuccessCode;
import com.amoa.server.domain.common.service.query.RegionQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController implements RegionControllerDocs {

    private final RegionQueryService regionQueryService;

    // 검색 기반 지역 조회
    @GetMapping
    @Override
    public ApiResponse<List<RegionInfo>> search(
            @RequestParam String keyword
    ) {
        List<RegionInfo> response = regionQueryService.search(keyword);

        return ApiResponse.onSuccess(RegionSuccessCode.REGION_FOUND, response);
    }

    // 현재 위치 기반 지역 조회
    @GetMapping("/present")
    @Override
    public ApiResponse<RegionInfo> getCurrentRegion(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude
    ) {
        return ApiResponse.onSuccess(
                RegionSuccessCode.REGION_FOUND,
                regionQueryService.getCurrentRegion(
                        latitude,
                        longitude
                )
        );
    }
}
