package com.amoa.server.domain.shop.controller;

import com.amoa.server.domain.shop.controller.docs.ShopOptionControllerDocs;
import com.amoa.server.domain.shop.dto.Request.ShopOptionReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopOptionResDTO;
import com.amoa.server.domain.shop.exception.code.ShopSuccessCode;
import com.amoa.server.domain.shop.service.command.ShopOptionCommandService;
import com.amoa.server.domain.shop.service.query.ShopOptionQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/shops")
public class ShopOptionController implements ShopOptionControllerDocs {

    private final ShopOptionCommandService shopOptionCommandService;
    private final ShopOptionQueryService shopOptionQueryService;

    @Override
    @PostMapping("/{shopId}/options")
    public ApiResponse<ShopOptionResDTO.OptionResult> createShopOption(
            @PathVariable Long shopId,
            @Valid @RequestBody ShopOptionReqDTO.ShopOptionCreateReqDTO request
    ) {
        return ApiResponse.onSuccess(
                ShopSuccessCode.SHOP_OPTION_CREATED,
                shopOptionCommandService.createShopOption(shopId, request)
        );
    }

    @Override
    @GetMapping("/{shopId}/options")
    public ApiResponse<ShopOptionResDTO.OptionListResult> getShopOptions(
            @PathVariable Long shopId
    ) {
        return ApiResponse.onSuccess(
                ShopSuccessCode.SHOP_OPTION_LIST_FOUND,
                shopOptionQueryService.getShopOptions(shopId)
        );
    }

    @Override
    @PatchMapping("/{shopId}/options/{optionId}")
    public ApiResponse<ShopOptionResDTO.OptionListResult> updateShopOption(
            @PathVariable Long shopId,
            @PathVariable Long optionId,
            @Valid @RequestBody ShopOptionReqDTO.UpdateRequest request
    ) {
        return ApiResponse.onSuccess(
                ShopSuccessCode.SHOP_OPTION_UPDATED,
                shopOptionCommandService.updateShopOption(
                        shopId,
                        optionId,
                        request
                )
        );
    }
}
