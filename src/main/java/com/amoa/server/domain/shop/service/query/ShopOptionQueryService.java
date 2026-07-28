package com.amoa.server.domain.shop.service.query;

import com.amoa.server.domain.shop.converter.ShopOptionConverter;
import com.amoa.server.domain.shop.dto.response.ShopOptionResDTO;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.shop.exception.ShopException;
import com.amoa.server.domain.shop.exception.code.ShopErrorCode;
import com.amoa.server.domain.shop.repository.ShopOptionRepository;
import com.amoa.server.domain.shop.repository.ShopRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopOptionQueryService {

    private final ShopRepository shopRepository;
    private final ShopOptionRepository shopOptionRepository;

    public ShopOptionResDTO.OptionListResult getShopOptions(
            Long shopId
    ) {
        if (!shopRepository.existsById(shopId)) {
            throw new ShopException(
                    ShopErrorCode.SHOP_NOT_FOUND
            );
        }

        List<ShopOption> options =
                shopOptionRepository
                        .findAllByShop_IdAndIsActiveTrueOrderByIdAsc(
                                shopId
                        );

        return ShopOptionConverter.toOptionListResult(options);
    }
}
