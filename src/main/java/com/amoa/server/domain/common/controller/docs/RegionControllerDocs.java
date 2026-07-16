package com.amoa.server.domain.common.controller.docs;

import com.amoa.server.domain.common.dto.response.RegionResDTO.SearchRegion;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Region", description = "지역 관련 API")
public interface RegionControllerDocs {

    @Operation(
            summary = "검색 기반 지역 조회",
            description = "동, 지하철역, 구 이름을 기반으로 카카오 Local API를 통해 지역을 검색합니다."
    )
    ApiResponse<List<SearchRegion>> search(
            @RequestParam String keyword
    );
}
