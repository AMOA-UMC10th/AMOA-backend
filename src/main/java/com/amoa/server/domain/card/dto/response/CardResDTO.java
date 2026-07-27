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

    // 카카오로 시작하기
    public record KakaoChannel(
            String kakaoChannelUrl
    ) {
    }

    // 아트 목록 조회
    public record CardList(
            Long totalCount,
            Integer size,
            List<CardInfo> cards,
            String nextCursor,  // 복합 커서
            boolean hasNext
    ) {
    }

    // 아트 목록 조회에서 카드 하나의 정보
    public record CardInfo(
            Long cardId,
            String shopName,
            String instagramUrl,
            ArtType artType,
            Integer minPrice,
            Integer maxPrice,
            String regionName,
            String createdMonth,
            boolean isLiked
    ) {
    }

    // 아트 상세 조회
    public record CardDetailResponse(
            CardDetail card
    ) {
    }

    public record CardDetail(
            Long cardId,
            Long shopId,
            String shopName,
            String instagramUrl,
            ArtType artType,
            List<DesignTagInfo> designTags,
            Integer minPrice,
            Integer maxPrice,
            String address,
            String createdMonth
    ) {
    }

    public record DesignTagInfo(
            Long designTagId,
            String name
    ) {
    }
}
