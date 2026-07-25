package com.amoa.server.domain.term.converter;

import com.amoa.server.domain.term.dto.response.TermDetailResDTO;
import com.amoa.server.domain.term.dto.response.TermListItemResDTO;
import com.amoa.server.domain.term.entity.Term;
import java.util.List;

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

    public static TermListItemResDTO toTermListItemResDTO(
            Term term
    ) {
        return TermListItemResDTO.builder()
                .termId(term.getId())
                .title(term.getTitle())
                .required(term.isRequired())
                .build();
    }

    public static List<TermListItemResDTO> toTermListItemResDTOList(
            List<Term> terms
    ) {
        return terms.stream()
                .map(TermConverter::toTermListItemResDTO)
                .toList();
    }
}