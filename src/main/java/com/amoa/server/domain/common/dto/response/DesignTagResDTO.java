package com.amoa.server.domain.common.dto.response;

import java.util.List;

public class DesignTagResDTO {

    public record DesignTagResponse(
            Long designTagId,
            String name
    ) {
    }

    public record DesignTagListResponse(
            List<DesignTagResponse> designTags
    ) {
    }
}