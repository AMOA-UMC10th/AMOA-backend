package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long>{
    Optional<UserCard> findByUserAndCard(User user, Card card);

    boolean existsByUserAndCard(User user, Card card);
}
