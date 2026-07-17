package com.amoa.server.domain.card.dto.request;

import com.amoa.server.domain.card.enums.ArtType;
import jakarta.validation.constraints.Min;
import java.util.List;

public class CardReqDTO {

    // 아트 등록
    public record createCard(
            Long shopId,
            String instagramUrl,
            Integer maxPrice,
            Integer minPrice,
            @Min(value = 1, message = "소요 시간은 1분 이상이어야 합니다.")
            Integer durationMinutes,
            Integer createdYear,
            Integer createdMonth,
            ArtType artType,
            List<Long> designTagIds
    ) {
    }
}
