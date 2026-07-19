package com.amoa.server.domain.shop.service.command;

import com.amoa.server.domain.shop.converter.ShopOptionConverter;
import com.amoa.server.domain.shop.dto.Request.ShopOptionReqDTO;
import com.amoa.server.domain.shop.dto.Request.ShopOptionReqDTO.ShopOptionCreateReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopOptionResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.ShopOptionRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopOptionCommandService {

    private final ShopRepository shopRepository;
    private final ShopOptionRepository shopOptionRepository;

    @Transactional
    public ShopOptionResDTO.OptionResult createShopOption(
            Long shopId,
            ShopOptionCreateReqDTO request
    ) {

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        if (shopOptionRepository.existsByShop_IdAndOptionName(
                shopId,
                request.optionName())) {

            throw new ShopException(
                    ShopErrorCode.DUPLICATE_SHOP_OPTION
            );
        }

        ShopOption option =
                ShopOptionConverter.toShopOption(shop, request);

        try {
            shopOptionRepository.saveAndFlush(option);
        } catch (DataIntegrityViolationException e) {
            throw new ShopException(
                    ShopErrorCode.DUPLICATE_SHOP_OPTION
            );
        }

        return ShopOptionConverter.toOptionResult(option);
    }

    @Transactional
    public ShopOptionResDTO.OptionListResult updateShopOption(
            Long shopId,
            Long optionId,
            ShopOptionReqDTO.UpdateRequest request
    ) {
        // 1. 샵 존재 여부 확인
        if (!shopRepository.existsById(shopId)) {
            throw new ShopException(
                    ShopErrorCode.SHOP_NOT_FOUND
            );
        }

        // 2. 해당 샵에 속한 활성 옵션 조회
        ShopOption option = shopOptionRepository
                .findByIdAndShop_IdAndIsActiveTrue(
                        optionId,
                        shopId
                )
                .orElseThrow(() -> new ShopException(
                        ShopErrorCode.SHOP_OPTION_NOT_FOUND
                ));

        // 3. 자기 자신을 제외한 옵션명 중복 확인
        boolean duplicated =
                shopOptionRepository
                        .existsByShop_IdAndOptionNameAndIdNot(
                                shopId,
                                request.optionName(),
                                optionId
                        );

        if (duplicated) {
            throw new ShopException(
                    ShopErrorCode.DUPLICATE_SHOP_OPTION
            );
        }

        // 4. 옵션 수정
        option.update(
                request.optionName(),
                request.optionPrice(),
                request.durationMinutes(),
                request.maxQuantity()
        );

        // 5. 수정된 옵션을 포함한 전체 활성 옵션 목록 조회
        List<ShopOption> options = shopOptionRepository
                .findAllByShop_IdAndIsActiveTrueOrderByIdAsc(shopId);

        return ShopOptionConverter.toOptionListResult(options);
    }
}
