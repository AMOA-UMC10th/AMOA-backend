package com.amoa.server.domain.shop.controller.docs;

import com.amoa.server.domain.shop.dto.Request.ShopOptionReqDTO.ShopOptionCreateReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopOptionResDTO.CreateResult;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Shop", description = "샵 관련 API")
public interface ShopOptionControllerDocs {
    @Operation(
            summary = "샵 옵션 등록 API",
            description = "샵에서 제공하는 추가 옵션을 등록합니다."
    )
    ApiResponse<CreateResult> createShopOption(
            Long shopId,
            ShopOptionCreateReqDTO request
    );
}
