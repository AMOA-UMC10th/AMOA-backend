package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.enums.ShopStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    // 샵 이름으로 검색 (어드민 목록 조회 시 keyword 검색)
    List<Shop> findByShopNameContaining(String keyword);

    // 상태로 필터링
    List<Shop> findByShopStatus(ShopStatus shopStatus);

    // 샵 이름 + 상태로 필터링
    List<Shop> findByShopNameContainingAndShopStatus(String keyword, ShopStatus shopStatus);
}