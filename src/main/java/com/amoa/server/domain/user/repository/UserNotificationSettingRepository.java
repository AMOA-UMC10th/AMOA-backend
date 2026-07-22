package com.amoa.server.domain.user.repository;

import com.amoa.server.domain.user.entity.mapping.UserNotificationSetting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationSettingRepository extends JpaRepository<UserNotificationSetting, Long> {

    List<UserNotificationSetting> findAllByUser_Id(Long userId);
}
