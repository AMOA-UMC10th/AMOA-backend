package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.card.entity.Card;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    Optional<UserCard> findByUserAndCard(User user, Card card);

    boolean existsByUserAndCard(User user, Card card);

    // 아트 목록 조회 시 사용자가 찜한 카드 조회
    List<UserCard> findByUserIdAndCardIdIn(Long userId, List<Long> cardIds);
}
