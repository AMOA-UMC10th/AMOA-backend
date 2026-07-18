package com.amoa.server.domain.card.converter;

import com.amoa.server.domain.card.dto.request.CardReqDTO;
import com.amoa.server.domain.card.dto.response.CardResDTO.CreateCard;
import com.amoa.server.domain.card.dto.response.CardResDTO.CreateCard.DesignTagRes;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.entity.mapping.CardDesignTag;
import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.shop.entity.Shop;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class CardConverter {
    // 년도와 월을 받아 해당 월의 첫일 LocalDate로 변환합니다.
    public LocalDate convertYearMonthToLocalDate(Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.atDay(1);
    }


    public Card toEntity(
            CardReqDTO.createCard request,
            Shop shop,
            LocalDate createdMonth
    ) {
        return Card.builder()
                .shop(shop)
                .instagramUrl(request.instagramUrl())
                .maxPrice(request.maxPrice())
                .minPrice(request.minPrice())
                .durationMinutes(
                        request.durationMinutes() != null
                                ? request.durationMinutes()
                                : 60
                )
                .createdMonth(createdMonth)
                .artType(request.artType())
                .likeCard(0)
                .build();
    }

    // Card 엔티티와 DesignTag 목록을 아트 등록 응답 DTO로 변환합니다.
    public CreateCard toCreateCardResponse(Card card, List<CardDesignTag> cardDesignTags) {
        List<DesignTagRes> designTagResList = cardDesignTags.stream()
                .map(cardDesignTag -> new DesignTagRes(
                        cardDesignTag.getDesignTag().getDesignTagId(),
                        cardDesignTag.getDesignTag().getName()
                ))
                .toList();

        return new CreateCard(
                card.getId(),
                card.getShop().getId(),
                card.getInstagramUrl(),
                card.getMaxPrice(),
                card.getMinPrice(),
                card.getDurationMinutes(),
                card.getCreatedMonth(),
                card.getArtType(),
                designTagResList,
                card.getCreatedAt()
        );
    }
}
