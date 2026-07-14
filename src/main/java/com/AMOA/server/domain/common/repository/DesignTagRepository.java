package com.amoa.server.domain.common.repository;

import com.amoa.server.domain.common.entity.DesignTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignTagRepository extends JpaRepository<DesignTag, Long> {

    // designtag_ids 목록으로 태그 조회 (샵 등록/수정 시 사용)
    List<DesignTag> findByDesignTagIdIn(List<Long> designTagIds);

    // designtag_id 오름차순으로 정렬
    List<DesignTag> findAllByOrderByDesignTagIdAsc();
}