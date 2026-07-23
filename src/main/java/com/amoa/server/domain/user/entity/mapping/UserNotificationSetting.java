package com.amoa.server.domain.user.entity.mapping;

import com.amoa.server.domain.user.entity.User;
import com.amoa.server.domain.user.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_notification_setting",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_notification_setting",
                        columnNames = {"user_id", "notification_type"}
                )
        }
)
public class UserNotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_notification_setting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 30)
    private NotificationType notificationType;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static UserNotificationSetting create(
            User user,
            NotificationType notificationType,
            boolean enabled
    ) {
        return UserNotificationSetting.builder()
                .user(user)
                .notificationType(notificationType)
                .enabled(enabled)
                .build();
    }
}