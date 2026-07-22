package com.amoa.server.domain.notice.converter;

import com.amoa.server.domain.notice.dto.response.NoticeDetailResDTO;
import com.amoa.server.domain.notice.entity.Notice;

public class NoticeConverter {

    public static NoticeDetailResDTO toNoticeDetailResDTO(
            Notice notice
    ) {
        return NoticeDetailResDTO.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}