package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.SavedShop;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedShopRepository extends JpaRepository<SavedShop, Long> {
    Optional<SavedShop> findByUserAndShop(User user, Shop shop);

    boolean existsByUserAndShop(User user, Shop shop);

    int countByShop(Shop shop);  // 샵 찜 수
}
