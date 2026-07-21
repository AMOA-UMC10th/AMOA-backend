package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.entity.Card;
import java.util.List;

public interface CardRepositoryCustom {

    List<Card> findCards(CardSearchRequest request);

    Long countCards(CardSearchRequest request);
}
