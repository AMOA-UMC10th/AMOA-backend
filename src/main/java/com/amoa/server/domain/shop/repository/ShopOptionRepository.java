package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.mapping.ShopOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOptionRepository
        extends JpaRepository<ShopOption, Long> {

    boolean existsByShop_IdAndOptionName(
            Long shopId,
            String optionName
    );
}