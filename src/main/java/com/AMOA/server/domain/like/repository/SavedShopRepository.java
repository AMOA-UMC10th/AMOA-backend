package com.AMOA.server.domain.like.repository;

import com.AMOA.server.domain.like.domain.SavedShop;
import com.AMOA.server.domain.user.domain.User;
import com.AMOA.server.domain.shop.domain.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedShopRepository extends JpaRepository<SavedShop, Long> {
    Optional<SavedShop> findByUserAndShop(User user, Shop shop);

    boolean existsByUserAndShop(User user, Shop shop);
}
