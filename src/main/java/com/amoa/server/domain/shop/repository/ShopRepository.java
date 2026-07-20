package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.shop.enums.ShopStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    //동시 예약 요청 시 검증
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Shop s where s.id = :shopId")
    Optional<Shop> findByIdWithLock(
            @Param("shopId") Long shopId
    );
}