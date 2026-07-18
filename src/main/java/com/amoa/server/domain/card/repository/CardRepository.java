package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

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
}
