package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOptionRepository
        extends JpaRepository<ShopOption, Long> {

    List<ShopOption> findAllByShop_IdAndIsActiveTrueOrderByIdAsc(
            Long shopId
    );
}