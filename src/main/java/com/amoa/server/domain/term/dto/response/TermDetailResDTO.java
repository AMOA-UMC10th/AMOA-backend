package com.amoa.server.domain.term.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TermDetailResDTO(
        Long termId,
        String title,
        String content,
        LocalDateTime createdAt
) {
}