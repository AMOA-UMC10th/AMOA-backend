package com.amoa.server.domain.shop.controller.docs;

import com.amoa.server.domain.shop.dto.Request.ShopOptionReqDTO.ShopOptionCreateReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopOptionResDTO;
import com.amoa.server.domain.shop.dto.Response.ShopOptionResDTO.OptionResult;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Shop", description = "샵 관련 API")
public interface ShopOptionControllerDocs {

    @Operation(
            summary = "샵 옵션 등록 API",
            description = "샵에서 제공하는 추가 옵션을 등록합니다."
    )
    ApiResponse<OptionResult> createShopOption(
            Long shopId,
            ShopOptionCreateReqDTO request
    );

    @Operation(
            summary = "샵 옵션 목록 조회 API",
            description = "특정 샵에서 제공하는 활성화된 추가 옵션 목록을 조회합니다."
    )
    ApiResponse<ShopOptionResDTO.OptionListResult> getShopOptions(
            @Parameter(description = "샵 ID", example = "1")
            Long shopId
    );
}
