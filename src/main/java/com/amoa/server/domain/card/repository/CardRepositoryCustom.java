package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.dto.request.CardReqDTO.CardSearchRequest;
import com.amoa.server.domain.card.dto.request.CardReqDTO.ShopCardSearchRequest;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.common.enums.ArtType;
import java.util.List;

public interface CardRepositoryCustom {

    List<Card> findCards(CardSearchRequest request, int size);

    Long countCards(CardSearchRequest request);

    Long countCardsExcludingConditions(CardSearchRequest broaderRequest, CardSearchRequest narrowerRequest);

    List<Card> findShopCards(ShopCardSearchRequest request, int size);

    Long countShopCards(Long shopId, ArtType artType);

    List<Card> findRecommendedCards(Card currentCard, int limit);
}
