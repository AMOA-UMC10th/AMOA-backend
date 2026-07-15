package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.mapping.CardDesignTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDesignTagRepository extends JpaRepository<CardDesignTag, Long> {
}
