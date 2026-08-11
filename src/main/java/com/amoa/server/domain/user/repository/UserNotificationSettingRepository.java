package com.amoa.server.domain.user.repository;

import com.amoa.server.domain.user.entity.mapping.UserNotificationSetting;
import com.amoa.server.domain.user.enums.NotificationType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {

    List<UserNotificationSetting> findAllByUser_Id(Long userId);

    Optional<UserNotificationSetting> findByUser_IdAndNotificationType(
            Long userId,
            NotificationType notificationType
    );

    void deleteAllByUser_Id(Long userId);
}
