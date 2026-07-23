package com.amoa.server.domain.term.controller;

import com.amoa.server.domain.term.controller.docs.TermControllerDocs;
import com.amoa.server.domain.term.dto.response.TermDetailResDTO;
import com.amoa.server.domain.term.exception.code.TermSuccessCode;
import com.amoa.server.domain.term.service.query.TermQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terms")
public class TermController implements TermControllerDocs {

    private final TermQueryService termQueryService;

    @Override
    @GetMapping("/{termId}")
    public ApiResponse<TermDetailResDTO> getTermDetail(
            @PathVariable Long termId
    ) {
        return ApiResponse.onSuccess(
                TermSuccessCode.TERM_DETAIL_OK,
                termQueryService.getTermDetail(termId)
        );
    }
}