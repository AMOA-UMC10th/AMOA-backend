package com.amoa.server.domain.notice.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NoticeDetailResDTO(
        Long noticeId,
        String title,
        String content,
        LocalDateTime createdAt
) {}