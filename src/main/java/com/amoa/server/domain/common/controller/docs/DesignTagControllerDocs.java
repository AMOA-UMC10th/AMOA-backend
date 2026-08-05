package com.amoa.server.domain.common.controller.docs;

import com.amoa.server.domain.common.dto.response.DesignTagResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "DesignTag", description = "디자인 태그 관련 API")
public interface DesignTagControllerDocs {

    @Operation(
            summary = "디자인 태그 목록 조회",
            description = "아트 검색, 온보딩, 샵 등록 등에서 공통으로 사용하는 디자인 태그 목록을 조회합니다."
    )
    ApiResponse<DesignTagResDTO.DesignTagListResponse> getDesignTags();
}
