package com.amoa.server.domain.common.controller;

import com.amoa.server.domain.common.controller.docs.DesignTagControllerDocs;
import com.amoa.server.domain.common.dto.response.DesignTagResDTO;
import com.amoa.server.domain.common.exception.code.DesignTagSuccessCode;
import com.amoa.server.domain.common.service.query.DesignTagQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/design-tags")
@RequiredArgsConstructor
public class DesignTagController implements DesignTagControllerDocs {

    private final DesignTagQueryService designTagQueryService;

    @Override
    @GetMapping
    public ApiResponse<DesignTagResDTO.DesignTagListResponse> getDesignTags() {
        return ApiResponse.onSuccess(
                DesignTagSuccessCode.DESIGN_TAG_LIST_FOUND,
                designTagQueryService.getDesignTags()
        );
    }
}