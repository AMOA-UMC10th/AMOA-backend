package com.amoa.server.domain.card.service.query;

import com.amoa.server.domain.card.dto.response.CardResDTO;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.exception.CardException;
import com.amoa.server.domain.card.exception.code.CardErrorCode;
import com.amoa.server.domain.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardQueryService {

    private final CardRepository cardRepository;

    @Transactional(readOnly = true)
    public CardResDTO.KakaoChannel getKakaoChannel(Long cardId) {

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new CardException(CardErrorCode.CARD_NOT_FOUND)
                );

        return new CardResDTO.KakaoChannel(
                card.getShop().getKakaoChannelUrl()
        );
    }
}
