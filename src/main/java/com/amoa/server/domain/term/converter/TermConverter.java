package com.amoa.server.domain.term.converter;

import com.amoa.server.domain.term.dto.response.TermDetailResDTO;
import com.amoa.server.domain.term.entity.Term;

public class TermConverter {

    public static TermDetailResDTO toTermDetailResDTO(
            Term term
    ) {
        return TermDetailResDTO.builder()
                .termId(term.getId())
                .title(term.getTitle())
                .content(term.getContent())
                .createdAt(term.getCreatedAt())
                .build();
    }
}