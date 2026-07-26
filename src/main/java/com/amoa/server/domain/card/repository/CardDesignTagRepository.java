package com.amoa.server.domain.card.repository;

import com.amoa.server.domain.card.entity.mapping.CardDesignTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDesignTagRepository extends JpaRepository<CardDesignTag, Long> {

    // 매칭 여부 확인 메서드 추가
    boolean existsByCard_IdAndDesignTag_IdIn(Long cardId, List<Long> designTagIds);

}
