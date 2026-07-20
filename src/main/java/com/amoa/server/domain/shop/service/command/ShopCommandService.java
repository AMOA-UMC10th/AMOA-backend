package com.amoa.server.domain.shop.service.command;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.common.repository.RegionRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.Request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.ShopDesignTagRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
import com.amoa.server.global.kakao.KakaoLocalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopCommandService {

    private final ShopRepository shopRepository;
    private final ShopDesignTagRepository shopDesignTagRepository;
    private final DesignTagRepository designTagRepository;
    private final RegionRepository regionRepository;
    private final KakaoLocalClient kakaoLocalClient;

    // POST /api/admin/shops - 샵 등록
    public ShopResDTO.CreateShopResponse createShop(ShopReqDTO.CreateShopRequest request) {

        // 1) 카카오 로컬 API로 주소 → 좌표 변환
        BigDecimal[] coordinates = kakaoLocalClient.getCoordinates(request.address());
        if (coordinates == null) {
            throw new ShopException(ShopErrorCode.SHOP_INVALID_ADDRESS);
        }
        BigDecimal latitude = coordinates[0];
        BigDecimal longitude = coordinates[1];

        // 2) 주소에서 행정구역 추출 후 Region 조회
        String[] depths = extractRegionDepth(request.address());

        Region region = regionRepository
                .findByFirstDepthAndSecondDepthAndThirdDepth(
                        depths[0],
                        depths[1],
                        depths[2]
                )
                .orElseThrow(() ->
                        new ShopException(ShopErrorCode.REGION_NOT_FOUND)
                );

        // 3) Shop Entity 생성 및 저장
        Shop shop = ShopConverter.toShop(request, region, latitude, longitude);
        shopRepository.save(shop);

        // 4) DesignTag 조회 및 ShopDesignTag 저장
        if (request.designtagIds() != null && !request.designtagIds().isEmpty()) {
            List<DesignTag> designTags = designTagRepository
                    .findByIdIn(request.designtagIds());

            if (designTags.size() != request.designtagIds().size()) {
                throw new ShopException(ShopErrorCode.SHOP_INVALID_DESIGN_TAG);
            }

            List<ShopDesignTag> shopDesignTags = designTags.stream()
                    .map(designTag -> ShopConverter.toShopDesignTag(shop, designTag))
                    .toList();
            shopDesignTagRepository.saveAll(shopDesignTags);
        }

        // 5) Response 반환
        return ShopConverter.toCreateShopResponse(shop);
    }

    // 주소에서 구/군 단위 파싱
    private String[] extractRegionDepth(String address) {

        if (address == null || address.isBlank()) {
            throw new ShopException(ShopErrorCode.SHOP_INVALID_ADDRESS);
        }

        String[] parts = address.split(" ");

        String firstDepth = null;
        String secondDepth = null;
        String thirdDepth = null;

        for (String part : parts) {

            // 시/도
            if (firstDepth == null && (part.endsWith("시") || part.endsWith("도"))) {
                firstDepth = part;
            }

            // 시/군/구
            if (secondDepth == null && (part.endsWith("시") || part.endsWith("군") || part.endsWith("구"))) {
                secondDepth = part;
            }

            // 읍/면/동
            if (thirdDepth == null && (part.endsWith("읍") || part.endsWith("면") || part.endsWith("동"))) {
                thirdDepth = part;
            }
        }

        if (firstDepth == null || secondDepth == null || thirdDepth == null) {
            throw new ShopException(
                    ShopErrorCode.SHOP_INVALID_ADDRESS
            );
        }

        return new String[]{
                firstDepth, secondDepth, thirdDepth
        };
    }
}