package com.amoa.server.domain.shop.converter;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.shop.dto.Request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.ShopDesignTag;
import com.amoa.server.domain.shop.enums.ShopStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class ShopConverter {

    // DesignTag Entity → DesignTagResponse DTO 변환
    public static ShopResDTO.DesignTagResponse toDesignTagResponse(DesignTag designTag) {
        return new ShopResDTO.DesignTagResponse(
                designTag.getDesignTagId(),
                designTag.getName()
        );
    }

    // DesignTag 리스트 → DesignTagListResponse DTO 변환
    public static ShopResDTO.DesignTagListResponse toDesignTagListResponse(List<DesignTag> designTags) {
        List<ShopResDTO.DesignTagResponse> designTagResponses = designTags.stream()
                .map(ShopConverter::toDesignTagResponse)
                .collect(Collectors.toList());
        return new ShopResDTO.DesignTagListResponse(designTagResponses);
    }

    // CreateShopRequest DTO → Shop Entity 변환
    public static Shop toShop(ShopReqDTO.CreateShopRequest request,
                              Region region,
                              BigDecimal latitude,
                              BigDecimal longitude) {
        return Shop.builder()
                .shopName(request.shopName())
                .kakaoChannelUrl(request.kakaoChannelUrl())
                .instagramUrl(request.instagramUrl())
                .profileImageUrl(request.profileImageUrl())
                .address(request.address())
                .latitude(latitude)
                .longitude(longitude)
                .shopPhoneNumber(request.shopPhoneNumber())
                .businessHours(request.businessHours())
                .shopStatus(ShopStatus.DRAFT)
                .region(region)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Shop Entity + DesignTag 리스트 → ShopDesignTag Entity 변환
    public static ShopDesignTag toShopDesignTag(Shop shop, DesignTag designTag) {
        return ShopDesignTag.builder()
                .shop(shop)
                .designTag(designTag)
                .build();
    }

    // Shop Entity → CreateShopResponse DTO 변환
    public static ShopResDTO.CreateShopResponse toCreateShopResponse(Shop shop) {
        return new ShopResDTO.CreateShopResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getAddress(),
                shop.getShopStatus(),
                shop.getCreatedAt()
        );
    }

    // 카카오 로컬 API 응답 → KakaoSearchResponse DTO 변환
    public static ShopResDTO.KakaoSearchResponse toKakaoSearchResponse(String placeName,
                                                                       String address,
                                                                       String phone,
                                                                       String businessHours) {
        return new ShopResDTO.KakaoSearchResponse(
                placeName,
                address,
                phone
        );
    }

    // Card Entity → CardResponse DTO 변환
    public static ShopResDTO.CardResponse toCardResponse(Card card, boolean isLiked) {
        return new ShopResDTO.CardResponse(
                card.getId(),
                card.getShop().getRegion().getSecondDepth() + " " + card.getShop().getRegion().getThirdDepth(),  // 구+동
                card.getMinPrice(),
                card.getMaxPrice(),
                card.getArtType() != null ? card.getArtType().name() : null,
                isLiked
        );
    }

    // Card 목록 → CardListResponse DTO 변환
    public static ShopResDTO.CardListResponse toCardListResponse(
            Shop shop,
            Page<Card> cards,
            List<ShopResDTO.CardResponse> cardResponses) {
        return new ShopResDTO.CardListResponse(
                shop.getId(),
                shop.getShopName(),
                cards.getTotalElements(),
                cards.getNumber(),
                cards.getSize(),
                cardResponses
        );
    }
}