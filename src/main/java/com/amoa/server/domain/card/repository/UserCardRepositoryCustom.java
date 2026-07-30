package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.UserCard;
import com.amoa.server.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCardRepositoryCustom {

    Page<UserCard> findLikedCardsByUserOrderByRecommended(
            User user,
            Pageable pageable
    );
}