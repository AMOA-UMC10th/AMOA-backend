package com.amoa.server.domain.notice.controller.docs;

import com.amoa.server.domain.notice.dto.response.NoticeDetailResDTO;
import com.amoa.server.domain.notice.dto.response.NoticeListResDTO;
import com.amoa.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Notice", description = "공지사항 관련 API")
public interface NoticeControllerDocs {

    @Operation(
            summary = "공지사항 상세 조회",
            description = "공지사항 ID를 이용하여 공지사항 상세 내용을 조회합니다."
    )
    ApiResponse<NoticeDetailResDTO> getNoticeDetail(
            @Parameter(
                    description = "공지사항 ID",
                    required = true
            )
            @PathVariable Long noticeId
    );

    @Operation(
            summary = "공지사항 목록 조회",
            description = "공지사항 목록을 최신순으로 조회합니다."
    )
    ApiResponse<List<NoticeListResDTO>> getNoticeList();
}