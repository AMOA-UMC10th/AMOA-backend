package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.entity.Card;
import java.util.List;
import java.util.Set;

public interface CardRepositoryCustom {

    List<Card> findCards(CardSearchRequest request, int size);

    Long countCards(CardSearchRequest request);

    Long countCardsExcludingIds(CardSearchRequest request, Set<Long> excludeIds);
}
