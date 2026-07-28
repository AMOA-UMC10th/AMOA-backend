package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.shop.entity.SavedShop;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavedShopRepository extends JpaRepository<SavedShop, Long> {

    Optional<SavedShop> findByUserAndShop(User user, Shop shop);

    // 최신순
    Page<SavedShop> findAllByUser(User user, Pageable pageable);

    // 인기순
    @Query("""
        SELECT ss
        FROM SavedShop ss
        JOIN ss.shop s
        WHERE ss.user = :user
        ORDER BY s.likeShop DESC
    """)
    Page<SavedShop> findAllByUserOrderByPopular(
            @Param("user") User user,
            Pageable pageable
    );

    // 가격 낮은순
    @Query("""
        SELECT ss
        FROM SavedShop ss
        JOIN ss.shop s
        JOIN Card c ON c.shop = s
        WHERE ss.user = :user
          AND c.deletedAt IS NULL
        GROUP BY ss
        ORDER BY MIN(c.minPrice) ASC
    """)
    Page<SavedShop> findAllByUserOrderByPriceAsc(
            @Param("user") User user,
            Pageable pageable
    );

    // 가격 높은순
    @Query("""
        SELECT ss
        FROM SavedShop ss
        JOIN ss.shop s
        JOIN Card c ON c.shop = s
        WHERE ss.user = :user
          AND c.deletedAt IS NULL
        GROUP BY ss
        ORDER BY MAX(c.maxPrice) DESC
    """)
    Page<SavedShop> findAllByUserOrderByPriceDesc(
            @Param("user") User user,
            Pageable pageable
    );

    boolean existsByUserAndShop(User user, Shop shop);

    int countByShop(Shop shop);
}