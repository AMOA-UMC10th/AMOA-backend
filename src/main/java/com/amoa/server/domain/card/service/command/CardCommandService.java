package com.amoa.server.domain.card.service.command;

import com.amoa.server.domain.card.converter.CardConverter;
import com.amoa.server.domain.card.dto.request.CardReqDTO;
import com.amoa.server.domain.card.dto.response.CardResDTO.CreateCard;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.entity.mapping.CardDesignTag;
import com.amoa.server.domain.card.exception.CardException;
import com.amoa.server.domain.card.exception.code.CardErrorCode;
import com.amoa.server.domain.card.repository.CardDesignTagRepository;
import com.amoa.server.domain.card.repository.CardRepository;
import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.repository.ShopRepository;
import com.amoa.server.global.apiPayload.code.GeneralErrorCode;
import com.amoa.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardCommandService {
    private final CardRepository cardRepository;
    private final CardDesignTagRepository cardDesignTagRepository;
    private final ShopRepository shopRepository;
    private final DesignTagRepository designTagRepository;
    private final CardConverter cardConverter;

    // 아트 등록
    @Transactional
    public CreateCard createCard(CardReqDTO.createCard request) {
        // 1. 샵 존재 여부 확인
        Shop shop = shopRepository.findById(request.shopId())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.BAD_REQUEST));

        // 2. 디자인 태그 존재 여부 확인 및 조회
        List<DesignTag> designTags = designTagRepository.findAllById(request.designTagIds());
        if (designTags.size() != request.designTagIds().size()) {
            throw new CardException(CardErrorCode.CARD_INVALID_DESIGN_TAG);
        }

        // 3. 년도와 월을 LocalDate로 변환 (해당 월의 첫 날)
        LocalDate createdMonth = cardConverter.convertYearMonthToLocalDate(
                request.createdYear(),
                request.createdMonth()
        );

        // 4. Card 엔티티 생성 및 저장
        Card card =
                cardConverter.toEntity(
                        request,
                        shop,
                        createdMonth
                );

        // 가격 범위 검증
        if (!card.isPriceRangeValid()) {
            throw new CardException(CardErrorCode.CARD_INVALID_PRICE_RANGE);
        }

        Card savedCard = cardRepository.save(card);

        // 5. CardDesignTag 연관 관계 저장
        List<CardDesignTag> cardDesignTags = designTags.stream()
                .map(designTag -> CardDesignTag.builder()
                        .card(savedCard)
                        .designTag(designTag)
                        .build())
                .toList();

        cardDesignTagRepository.saveAll(cardDesignTags);

        // 6. 응답 DTO 변환 및 반환
        return cardConverter.toCreateCardResponse(savedCard, cardDesignTags);
    }
}