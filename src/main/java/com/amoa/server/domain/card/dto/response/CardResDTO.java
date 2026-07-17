package com.amoa.server.domain.card.dto.response;

import com.amoa.server.domain.card.enums.ArtType;
import jakarta.validation.constraints.Min;
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
            @Min(value = 1, message = "소요 시간은 1분 이상이어야 합니다.")
            Integer durationMinutes,
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
