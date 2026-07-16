package com.amoa.server.domain.shop.repository;

import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopDesignTagRepository extends JpaRepository<ShopDesignTag, Long> {

    // 샵 ID로 연결된 태그 전체 조회
    @EntityGraph(attributePaths = {"designTag"})
    List<ShopDesignTag> findByShop_ShopId(Long shopId);

    // 샵 ID로 연결된 태그 전체 삭제 (수정 시 사용)
    void deleteByShop_ShopId(Long shopId);
}