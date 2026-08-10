package com.amoa.server.domain.common.controller.docs;

import com.amoa.server.domain.common.dto.response.RegionResDTO.RegionInfo;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Region", description = "지역 관련 API")
public interface RegionControllerDocs {

    @Operation(
            summary = "검색 기반 지역 조회",
            description = """
                    키워드를 기반으로 등록된 법정동 지역 목록을 조회합니다.
                    
                    - 키워드로 검색에 성공했지만, 결과가 없으면 빈 리스트를 반환합니다.
                    """
    )
    ApiResponse<List<RegionInfo>> search(
            @RequestParam String keyword
    );


    @Operation(
            summary = "현재 위치 기반 지역 조회",
            description = "위도와 경도를 기준으로 현재 위치의 법정동 지역을 조회합니다."
    )
    ApiResponse<RegionInfo> getCurrentRegion(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude
    );
}
