package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import com.amoa.server.domain.shop.enums.ShopOptionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOptionRepository
        extends JpaRepository<ShopOption, Long> {

    boolean existsByShop_IdAndOptionNameAndOptionTypeAndIsActiveTrue(
            Long shopId,
            String optionName,
            ShopOptionType optionType
    );

    List<ShopOption> findAllByShop_IdAndIsActiveTrueOrderByIdAsc(
            Long shopId
    );

    Optional<ShopOption> findByIdAndShop_IdAndIsActiveTrue(
            Long optionId,
            Long shopId
    );

    boolean existsByShop_IdAndOptionNameAndOptionTypeAndIdNotAndIsActiveTrue(
            Long shopId,
            String optionName,
            ShopOptionType optionType,
            Long optionId
    );
}