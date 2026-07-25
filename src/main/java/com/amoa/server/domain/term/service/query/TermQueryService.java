package com.amoa.server.domain.term.service.query;

import com.amoa.server.domain.term.converter.TermConverter;
import com.amoa.server.domain.term.dto.response.TermDetailResDTO;
import com.amoa.server.domain.term.dto.response.TermListItemResDTO;
import com.amoa.server.domain.term.entity.Term;
import com.amoa.server.domain.term.exception.code.TermErrorCode;
import com.amoa.server.domain.term.repository.TermRepository;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermQueryService {

    private final TermRepository termRepository;

    public TermDetailResDTO getTermDetail(
            Long termId
    ) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() ->
                        new GeneralException(
                                TermErrorCode.TERM_NOT_FOUND
                        )
                );

        return TermConverter.toTermDetailResDTO(term);
    }

    public List<TermListItemResDTO> getTermList() {
        List<Term> terms = termRepository.findAllByOrderByDisplayOrderAscIdAsc();

        return TermConverter.toTermListItemResDTOList(terms);
    }
}