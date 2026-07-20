package com.amoa.server.domain.shop.service.command;

import com.amoa.server.domain.shop.converter.ShopOptionConverter;
import com.amoa.server.domain.shop.dto.Request.ShopOptionReqDTO.ShopOptionCreateReqDTO;
import com.amoa.server.domain.shop.dto.Response.ShopOptionResDTO;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.ShopOptionRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
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
}
