package com.amoa.server.domain.term.controller.docs;

import com.amoa.server.domain.term.dto.response.TermDetailResDTO;
import com.amoa.server.domain.term.dto.response.TermListItemResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Term", description = "이용약관 관련 API")
public interface TermControllerDocs {

    @Operation(
            summary = "이용약관 상세 조회",
            description = "이용약관 ID를 이용하여 약관의 상세 내용을 조회합니다."
    )
    ApiResponse<TermDetailResDTO> getTermDetail(
            @Parameter(
                    description = "이용약관 ID",
                    required = true,
                    example = "1"
            )
            @PathVariable Long termId
    );

    @Operation(
            summary = "이용약관 목록 조회",
            description = "이용약관 목록을 조회합니다."
    )
    ApiResponse<List<TermListItemResDTO>> getTermList();
}