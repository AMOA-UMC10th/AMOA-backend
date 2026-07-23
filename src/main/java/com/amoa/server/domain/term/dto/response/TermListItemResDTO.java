package com.amoa.server.domain.term.dto.response;

import lombok.Builder;

@Builder
public record TermListItemResDTO(
        Long termId,
        String title
) {}