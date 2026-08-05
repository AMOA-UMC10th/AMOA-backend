package com.amoa.server.domain.shop.converter;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.common.converter.DesignTagConverter;
import com.amoa.server.domain.common.dto.response.DesignTagResDTO;
import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.shop.dto.request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.response.ShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import com.amoa.server.domain.shop.enums.ShopStatus;

import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.global.kakao.dto.response.KakaoAddressResDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShopConverter {

    // CreateShopRequest DTO → Shop Entity 변환
    public static Shop toShop(ShopReqDTO.CreateShopRequest request,
                              Region region,
                              BigDecimal latitude,
                              BigDecimal longitude,
                              String legalCode) {
        return Shop.builder()
                .shopName(request.shopName())
                .kakaoChannelUrl(request.kakaoChannelUrl())
                .instagramUrl(request.instagramUrl())
                .profileImageUrl(request.profileImageUrl())
                .address(request.address())
                .latitude(latitude)
                .longitude(longitude)
                .legalCode(legalCode)
                .shopPhoneNumber(request.shopPhoneNumber())
                .businessHours(request.businessHours())
                .depositAmount(request.depositAmount())
                //관리자 검수 시에는 .shopStatus(ShopStatus.DRAFT) 사용
                .shopStatus(ShopStatus.ACTIVE)
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

    public static ShopResDTO.ShopAddressSearchResponse toAddressSearchResponse(
            KakaoAddressResDTO.Document document
    ) {
        if (document.address() == null) {
            throw new ShopException(ShopErrorCode.ADDRESS_NOT_FOUND);
        }

        String roadAddress = document.roadAddress() != null
                ? document.roadAddress().addressName()
                : null;

        return ShopResDTO.ShopAddressSearchResponse.builder()
                .roadAddress(roadAddress)
                .jibunAddress(document.address().addressName())
                .latitude(new BigDecimal(document.y()))
                .longitude(new BigDecimal(document.x()))
                .legalCode(document.address().legalCode())
                .region1DepthName(document.address().region1DepthName())
                .region2DepthName(document.address().region2DepthName())
                .region3DepthName(document.address().region3DepthName())
                .build();
    }

    // Card Entity → CardResponse DTO 변환
    public static ShopResDTO.CardResponse toCardResponse(Card card, boolean isLiked) {
        return new ShopResDTO.CardResponse(
                card.getId(),
                card.getShop().getRegion().getThirdDepth(),   // 동만
                card.getMinPrice(),
                card.getMaxPrice(),
                card.getArtType() != null ? card.getArtType().name() : null,
                isLiked
        );
    }

    // Card 목록 → CardListResponse DTO 변환
    public static ShopResDTO.CardListResponse toCardListResponse(
            Shop shop,
            Long totalCount,
            List<ShopResDTO.CardResponse> cardResponses,
            String nextCursor,
            boolean hasNext
    ) {
        return new ShopResDTO.CardListResponse(
                shop.getId(),
                shop.getShopName(),
                totalCount,
                cardResponses,
                nextCursor,
                hasNext
        );
    }

    // Shop Entity → ShopDetailResponse DTO 변환
    public static ShopResDTO.ShopDetailResponse toShopDetailResponse(
            Shop shop,
            List<DesignTag> designTags,
            int cardLikeCount,
            int shopLikeCount,
            boolean isLiked) {

        List<DesignTagResDTO.DesignTagResponse> designTagResponses = designTags.stream()
                .map(DesignTagConverter::toDesignTagResponse)
                .toList();

        return new ShopResDTO.ShopDetailResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getAddress(),
                shop.getShopPhoneNumber(),
                shop.getBusinessHours(),
                designTagResponses,
                cardLikeCount,
                shopLikeCount,
                isLiked
        );
    }
}