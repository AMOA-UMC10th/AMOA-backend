package com.amoa.server.domain.notice.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NoticeListResDTO(
        Long noticeId,
        String title,
        LocalDateTime createdAt
) {
}