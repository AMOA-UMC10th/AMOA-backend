package com.amoa.server.domain.common.repository;

import com.amoa.server.domain.common.entity.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    // 지역 검색
    List<Region> findByFirstDepthContainingOrSecondDepthContainingOrThirdDepthContaining(
            String firstDepth,
            String secondDepth,
            String thirdDepth
    );

    Optional<Region> findByLegalCode(String legalCode);

    // 샵 등록 시 정확한 행정구역 조회
    Optional<Region> findByFirstDepthAndSecondDepthAndThirdDepth(
            String firstDepth,
            String secondDepth,
            String thirdDepth
    );
}