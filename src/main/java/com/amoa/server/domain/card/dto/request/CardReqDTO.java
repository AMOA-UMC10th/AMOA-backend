package com.amoa.server.domain.card.dto.request;

import com.amoa.server.domain.common.enums.SortType;
import jakarta.validation.constraints.Min;
import com.amoa.server.domain.common.enums.ArtType;
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

    // 아트 목록 조회
    public record CardSearchRequest(
            List<Long> regionIds,
            Integer minPrice,
            Integer maxPrice,
            ArtType artType,
            List<Long> designTagIds,
            SortType sort,
            String cursor, // 복합 커서
            Integer size
    ) {
    }
}
