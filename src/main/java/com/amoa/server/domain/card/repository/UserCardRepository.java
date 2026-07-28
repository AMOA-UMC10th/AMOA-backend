package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {

    Optional<UserCard> findByUserAndCard(User user, Card card);

    boolean existsByUserAndCard(User user, Card card);

    // 아트 목록 조회 시 사용자가 찜한 카드 조회
    List<UserCard> findByUserIdAndCardIdIn(Long userId, List<Long> cardIds);

    // 최신순
    @EntityGraph(attributePaths = {
            "card",
            "card.shop",
            "card.shop.region"
    })
    Page<UserCard> findAllByUser(User user, Pageable pageable);

    // 인기순
    @EntityGraph(attributePaths = {
            "card",
            "card.shop",
            "card.shop.region"
    })
    @Query("""
        SELECT uc
        FROM UserCard uc
        JOIN uc.card c
        WHERE uc.user = :user
        ORDER BY c.likeCard DESC
    """)
    Page<UserCard> findLikedCardsByUserOrderByPopular(
            @Param("user") User user,
            Pageable pageable
    );

    // 가격 낮은순
    @EntityGraph(attributePaths = {
            "card",
            "card.shop",
            "card.shop.region"
    })
    @Query("""
        SELECT uc
        FROM UserCard uc
        JOIN uc.card c
        WHERE uc.user = :user
        ORDER BY c.minPrice ASC
    """)
    Page<UserCard> findLikedCardsByUserOrderByPriceAsc(
            @Param("user") User user,
            Pageable pageable
    );

    // 가격 높은순
    @EntityGraph(attributePaths = {
            "card",
            "card.shop",
            "card.shop.region"
    })
    @Query("""
        SELECT uc
        FROM UserCard uc
        JOIN uc.card c
        WHERE uc.user = :user
        ORDER BY c.maxPrice DESC
    """)
    Page<UserCard> findLikedCardsByUserOrderByPriceDesc(
            @Param("user") User user,
            Pageable pageable
    );
}