package com.amoa.server.domain.user.repository;

import com.amoa.server.domain.user.entity.mapping.UserAgreement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAgreementRepository
        extends JpaRepository<UserAgreement, Long> {

    List<UserAgreement> findAllByUser_Id(Long userId);

    boolean existsByUser_IdAndTerm_IdAndAgreedVersion(
            Long userId,
            Long termId,
            String agreedVersion
    );
}