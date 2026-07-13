package com.AMOA.server.domain.shop.converter;

import com.AMOA.server.domain.common.entity.DesignTag;
import com.AMOA.server.domain.common.entity.Region;
import com.AMOA.server.domain.shop.dto.Request.ShopReqDTO;
import com.AMOA.server.domain.shop.dto.Response.ShopResDTO;
import com.AMOA.server.domain.shop.entity.Shop;
import com.AMOA.server.domain.shop.entity.ShopDesignTag;
import com.AMOA.server.domain.shop.enums.ShopStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                shop.getShopId(),
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
                phone,
                businessHours
        );
    }
}