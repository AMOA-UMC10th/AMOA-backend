package com.AMOA.server.domain.common.repository;

import com.AMOA.server.domain.common.entity.DesignTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignTagRepository extends JpaRepository<DesignTag, Long> {
}