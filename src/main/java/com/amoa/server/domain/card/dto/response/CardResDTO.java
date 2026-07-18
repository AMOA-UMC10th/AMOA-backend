package com.amoa.server.domain.card.dto.response;

import com.amoa.server.domain.common.enums.ArtType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class CardResDTO {
    // 아트 등록
    @Builder
    public record CreateCard(
            Long cardId,
            Long shopId,
            String instagramUrl,
            Integer maxPrice,
            Integer minPrice,
            LocalDate createdYearMonth,
            ArtType artType,
            List<DesignTagRes> designTags,
            LocalDateTime createdAt
    ) {
        public record DesignTagRes(
                Long designTagId,
                String name
        ) {
        }
    }
}
