package com.amoa.server.domain.shop.service.command;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.common.repository.DesignTagRepository;
import com.amoa.server.domain.common.repository.RegionRepository;
import com.amoa.server.domain.shop.converter.ShopConverter;
import com.amoa.server.domain.shop.dto.Request.ShopReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.ShopDesignTag;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.ShopDesignTagRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
import com.amoa.server.global.kakao.KakaoLocalClient;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

        // 2) 카카오 검색 결과에서 지역명 파싱 후 Region 조회
        String regionName = extractRegionName(request.address());
        // 예: "서울 성동구 성수동 123" → "성동구"

        Region region;
        try {
            region = regionRepository.findByName(regionName)
                    .orElseGet(() -> regionRepository.save(
                            Region.builder()
                                    .name(regionName)
                                    .createdAt(LocalDateTime.now())
                                    .updatedAt(LocalDateTime.now())
                                    .build()
                    ));
        } catch (DataIntegrityViolationException e) {
            // 동시 INSERT로 UNIQUE 제약 위반 시 기존 region 재조회
            region = regionRepository.findByName(regionName)
                    .orElseThrow(() -> new ShopException(ShopErrorCode.REGION_NOT_FOUND));
        }

        // 3) Shop Entity 생성 및 저장
        Shop shop = ShopConverter.toShop(request, region, latitude, longitude);
        shopRepository.save(shop);

        // 4) DesignTag 조회 및 ShopDesignTag 저장
        if (request.designtagIds() != null && !request.designtagIds().isEmpty()) {
            List<DesignTag> designTags = designTagRepository
                    .findByDesignTagIdIn(request.designtagIds());

            if (designTags.size() != request.designtagIds().size()) {
                throw new ShopException(ShopErrorCode.SHOP_INVALID_DESIGNTAG);
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
    private String extractRegionName(String address) {
        if (address == null || address.isBlank()) {
            throw new ShopException(ShopErrorCode.SHOP_INVALID_ADDRESS);
        }

        String[] parts = address.split(" ");

        // 1순위: 구/군 먼저 탐색
        for (String part : parts) {
            if (part.endsWith("구") || part.endsWith("군")) {
                return part;
            }
        }

        // 2순위: 구/군 없을 때만 시 탐색
        for (String part : parts) {
            if (part.endsWith("시")) {
                return part;
            }
        }

        // 구/군/시 모두 없으면 예외
        throw new ShopException(ShopErrorCode.SHOP_INVALID_ADDRESS);
    }
}