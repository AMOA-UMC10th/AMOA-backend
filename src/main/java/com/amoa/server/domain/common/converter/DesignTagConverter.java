package com.amoa.server.domain.common.converter;

import com.amoa.server.domain.common.dto.response.DesignTagResDTO;
import com.amoa.server.domain.common.entity.DesignTag;
import java.util.List;

public class DesignTagConverter {

    public static DesignTagResDTO.DesignTagResponse toDesignTagResponse(
            DesignTag designTag
    ) {
        return new DesignTagResDTO.DesignTagResponse(
                designTag.getId(),
                designTag.getName()
        );
    }

    public static DesignTagResDTO.DesignTagListResponse toDesignTagListResponse(
            List<DesignTag> designTags
    ) {
        List<DesignTagResDTO.DesignTagResponse> responses =
                designTags.stream()
                        .map(DesignTagConverter::toDesignTagResponse)
                        .toList();

        return new DesignTagResDTO.DesignTagListResponse(responses);
    }
}