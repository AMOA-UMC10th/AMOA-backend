package com.amoa.server.domain.notice.converter;

import com.amoa.server.domain.notice.dto.response.NoticeDetailResDTO;
import com.amoa.server.domain.notice.dto.response.NoticeListResDTO;
import com.amoa.server.domain.notice.entity.Notice;
import java.util.List;

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

    public static NoticeListResDTO toNoticeListResDTO(
            Notice notice
    ) {
        return NoticeListResDTO.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .createdAt(notice.getCreatedAt())
                .build();
    }

    public static List<NoticeListResDTO> toNoticeListResDTOList(
            List<Notice> notices
    ) {
        return notices.stream()
                .map(NoticeConverter::toNoticeListResDTO)
                .toList();
    }
}