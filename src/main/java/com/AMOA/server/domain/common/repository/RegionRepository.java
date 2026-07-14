package com.AMOA.server.domain.common.repository;

import com.AMOA.server.domain.common.entity.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name);
}