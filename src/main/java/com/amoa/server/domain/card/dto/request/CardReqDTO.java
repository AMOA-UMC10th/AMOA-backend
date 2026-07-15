package com.amoa.server.domain.card.dto.request;

import com.amoa.server.domain.card.enums.ArtType;
import java.util.List;

public class CardReqDTO {

    // 아트 등록
    public record createCard(
            Long shopId,
            String instagramUrl,
            Integer maxPrice,
            Integer minPrice,
            Integer createdYear,
            Integer createdMonth,
            ArtType artType,
            List<Long> designTagIds
    ) {
    }
}
