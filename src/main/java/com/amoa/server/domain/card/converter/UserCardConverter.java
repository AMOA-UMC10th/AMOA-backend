package com.amoa.server.domain.card.converter;

import com.amoa.server.domain.card.dto.response.UserCardResDTO;
import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.common.entity.Region;

public class UserCardConverter {

    public static UserCardResDTO.LikedCardResponse toLikedCardResponse(
            UserCard userCard
    ) {

        Card card = userCard.getCard();
        Shop shop = card.getShop();
        Region region = shop.getRegion();

        return new UserCardResDTO.LikedCardResponse(
                card.getId(),
                card.getInstagramUrl(),
                card.getArtType().name(),
                shop.getShopName(),
                region.getThirdDepth(),
                card.getMinPrice(),
                card.getMaxPrice(),
                userCard.getCreatedAt()
        );
    }
}