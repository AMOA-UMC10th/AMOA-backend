package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.Card;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.common.enums.ArtType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, CardRepositoryCustom {

    @Query("""
            SELECT c
            FROM Card c
            JOIN FETCH c.shop
            WHERE c.id = :cardId
              AND c.deletedAt IS NULL
            """)
    Optional<Card> findByIdWithShop(
            @Param("cardId") Long cardId
    );

    // 아트 추천 목록 조회용: shop, region, 디자인 태그까지 함께 fetch
    @Query("""
            SELECT c
            FROM Card c
            JOIN FETCH c.shop s
            JOIN FETCH s.region
            LEFT JOIN FETCH c.cardDesignTags cdt
            LEFT JOIN FETCH cdt.designTag
            WHERE c.id = :cardId
              AND c.deletedAt IS NULL
            """)
    Optional<Card> findByIdWithShopRegionAndTags(
            @Param("cardId") Long cardId
    );

    
    List<Card> findTop5ByShopAndDeletedAtIsNullOrderByCreatedAtDesc(
            Shop shop
    );

    // 샵 ID로 카드 목록 조회 (art_type 필터링 없이 전체)
    Page<Card> findByShop_IdAndDeletedAtIsNull(Long shopId, Pageable pageable);

    // 샵 ID + art_type으로 카드 목록 조회
    Page<Card> findByShop_IdAndArtTypeAndDeletedAtIsNull(Long shopId, ArtType artType, Pageable pageable);

    @Modifying
    @Query("UPDATE Card c SET c.likeCard = c.likeCard + 1 WHERE c.id = :cardId")
    void increaseLikeCount(@Param("cardId") Long cardId);

    @Modifying
    @Query("UPDATE Card c SET c.likeCard = c.likeCard - 1 WHERE c.id = :cardId")
    void decreaseLikeCount(@Param("cardId") Long cardId);

    // 샵의 전체 카드 찜 수
    @Query("SELECT COUNT(uc) FROM UserCard uc WHERE uc.card.shop.id = :shopId")
    int countCardLikesByShopId(@Param("shopId") Long shopId);
}
