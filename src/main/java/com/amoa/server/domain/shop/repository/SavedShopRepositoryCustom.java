package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.SavedShop;
import com.amoa.server.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavedShopRepositoryCustom {

    Page<SavedShop> findLikedShopsByUserOrderByRecommended(
            User user,
            Pageable pageable
    );

    boolean existsRecommendedShopByUser(User user);
}