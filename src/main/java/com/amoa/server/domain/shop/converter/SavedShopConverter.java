package com.amoa.server.domain.shop.converter;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.shop.dto.Response.SavedShopResDTO;
import com.amoa.server.domain.shop.entity.SavedShop;

import java.util.List;

public class SavedShopConverter {

    //SavedShop Entity -> LikedShopResponse DTO 변환
    public static SavedShopResDTO.LikedShopResponse toLikedShopResponse(
            SavedShop savedShop,
            List<SavedShopResDTO.CardPreviewDTO> cards
    ) {
        return SavedShopResDTO.LikedShopResponse.builder()
                .shopId(savedShop.getShop().getId())
                .shopName(savedShop.getShop().getShopName())
                .profileImageUrl(savedShop.getShop().getProfileImageUrl())
                .likedAt(savedShop.getCreatedAt())
                .regionName(savedShop.getShop().getRegion().getSecondDepth())
                .cards(cards)
                .build();

    }

    //Card Entity -> CardPreviewDTO 변환
    public static SavedShopResDTO.CardPreviewDTO toCardPreviewDTO(Card card){
        return SavedShopResDTO.CardPreviewDTO.builder()
                .cardId(card.getId())
                .instagramUrl(card.getInstagramUrl())
                .artType(card.getArtType())
                .maxPrice(card.getMaxPrice())
                .minPrice(card.getMinPrice())
                .build();
    }
}
