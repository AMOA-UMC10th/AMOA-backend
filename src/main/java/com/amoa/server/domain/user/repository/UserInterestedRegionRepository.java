package com.amoa.server.domain.user.repository;

import com.amoa.server.domain.user.entity.mapping.UserRegion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestedRegionRepository extends JpaRepository<UserRegion, Long> {

    List<UserRegion> findAllByUser_Id(Long userId);

    void deleteAllByUser_Id(Long userId);
}
