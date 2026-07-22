package com.amoa.server.domain.notice.controller;

import com.amoa.server.domain.notice.controller.docs.NoticeControllerDocs;
import com.amoa.server.domain.notice.dto.response.NoticeDetailResDTO;
import com.amoa.server.domain.notice.exception.code.NoticeSuccessCode;
import com.amoa.server.domain.notice.service.query.NoticeQueryService;
import com.amoa.server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController implements NoticeControllerDocs {

    private final NoticeQueryService noticeQueryService;

    @Override
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailResDTO> getNoticeDetail(
            @PathVariable Long noticeId
    ) {
        NoticeDetailResDTO result =
                noticeQueryService.getNoticeDetail(noticeId);

        return ApiResponse.onSuccess(
                NoticeSuccessCode.NOTICE_DETAIL_OK,
                result
        );
    }
}