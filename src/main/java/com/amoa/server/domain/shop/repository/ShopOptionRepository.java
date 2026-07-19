package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOptionRepository
        extends JpaRepository<ShopOption, Long> {

    boolean existsByShop_IdAndOptionName(
            Long shopId,
            String optionName
    );

    List<ShopOption> findAllByShop_IdAndIsActiveTrueOrderByIdAsc(
            Long shopId
    );

    Optional<ShopOption> findByIdAndShop_IdAndIsActiveTrue(
            Long optionId,
            Long shopId
    );

    boolean existsByShop_IdAndOptionNameAndIdNot(
            Long shopId,
            String optionName,
            Long optionId
    );
}