package com.AMOA.server.domain.card.converter;

import com.AMOA.server.domain.card.dto.request.CardReqDTO;
import com.AMOA.server.domain.card.dto.response.CardResDTO.CreateCard;
import com.AMOA.server.domain.card.dto.response.CardResDTO.CreateCard.DesignTagRes;
import com.AMOA.server.domain.card.entity.Card;
import com.AMOA.server.domain.card.enums.ArtDesign;
import com.AMOA.server.domain.common.entity.DesignTag;
import com.AMOA.server.domain.shop.entity.Shop;
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
                .createdMonth(createdMonth)
                .artType(request.artType())
                .artDesign(ArtDesign.SIMPLE)
                .likeCard(0)
                .build();
    }

    // Card 엔티티와 DesignTag 목록을 아트 등록 응답 DTO로 변환합니다.
    public CreateCard toCreateCardResponse(Card card, List<DesignTag> designTags) {
        List<DesignTagRes> designTagResList = designTags.stream()
                .map(designTag -> new DesignTagRes(
                        designTag.getId(),
                        designTag.getName()
                ))
                .toList();

        return new CreateCard(
                card.getId(),
                card.getShop().getId(),
                card.getInstagramUrl(),
                card.getMaxPrice(),
                card.getMinPrice(),
                card.getCreatedMonth(),
                card.getArtType(),
                designTagResList,
                card.getCreatedAt()
        );
    }
}
