package com.amoa.server.domain.user.repository;

import com.amoa.server.domain.user.entity.mapping.UserDesignTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDesignTagRepository extends JpaRepository<UserDesignTag, Long> {

    List<UserDesignTag> findAllByUser_Id(Long userId);
}