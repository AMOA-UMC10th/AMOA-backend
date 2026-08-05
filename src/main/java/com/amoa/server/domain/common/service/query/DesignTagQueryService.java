package com.amoa.server.domain.common.service.query;

import com.amoa.server.domain.common.converter.DesignTagConverter;
import com.amoa.server.domain.common.dto.response.DesignTagResDTO;
import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DesignTagQueryService {

    private final DesignTagRepository designTagRepository;

    public DesignTagResDTO.DesignTagListResponse getDesignTags() {
        List<DesignTag> designTags =
                designTagRepository.findAllByOrderByIdAsc();

        return DesignTagConverter.toDesignTagListResponse(designTags);
    }
}