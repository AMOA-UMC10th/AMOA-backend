package com.amoa.server.domain.user.repository;

import com.amoa.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM user
                    WHERE social_uid = :socialUid
                    """,
            nativeQuery = true
    )
    Optional<User> findBySocialUidIncludingInactive(
            @Param("socialUid") String socialUid
    );

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndIdNot(String nickname, Long userId);
}
