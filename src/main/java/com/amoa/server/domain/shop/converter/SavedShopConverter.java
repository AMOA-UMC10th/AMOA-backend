package com.amoa.server.domain.shop.converter;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.shop.dto.response.SavedShopResDTO;
import com.amoa.server.domain.shop.entity.SavedShop;

import java.util.List;

public class SavedShopConverter {

    //SavedShop Entity -> LikedShopResponse DTO 변환
    public static SavedShopResDTO.LikedShopResponse toLikedShopResponse(
            SavedShop savedShop,
            List<SavedShopResDTO.CardPreviewDTO> cards
    ) {
        return new SavedShopResDTO.LikedShopResponse(
                savedShop.getShop().getId(),
                savedShop.getShop().getShopName(),
                savedShop.getCreatedAt(),
                savedShop.getShop().getProfileImageUrl(),
                savedShop.getShop().getRegion().getThirdDepth(),
                cards
        );

    }

    //Card Entity -> CardPreviewDTO 변환
    public static SavedShopResDTO.CardPreviewDTO toCardPreviewDTO(Card card){
        return new SavedShopResDTO.CardPreviewDTO(
                card.getId(),
                card.getInstagramUrl(),
                card.getArtType(),
                card.getMaxPrice(),
                card.getMinPrice()
        );
    }
}
