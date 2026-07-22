package com.amoa.server.domain.user.entity;

import com.amoa.server.domain.user.enums.Role;
import com.amoa.server.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_user_social_uid",
                        columnNames = "social_uid"
                )},
                indexes = {
                @Index(name = "idx_user_nickname", columnList = "nickname")
        }
)

@SQLDelete(sql = "UPDATE user SET is_active = false WHERE user_id = ?") // delete()시 hard delete 하는 것이 아닌 soft delete를 진행
@SQLRestriction("is_active = true") // 조회시 isActive 필드가 true인 데이터만 조회

public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "nickname", unique = true, length = 50)
    private String nickname;

    @NotBlank
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "social_uid", nullable = false, length = 255)
    private String socialUid;

    @Column(name = "user_phone_number", length = 20)
    private String userPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 100)
    @Builder.Default
    private Role role = Role.NEW_USER;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // 온보딩 완료
    public void completeOnboarding() {
        this.role = Role.USER;
    }

    // 계정 재활성화
    public void reactivate() {
        this.isActive = true;
        this.role = Role.NEW_USER;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.userPhoneNumber = phoneNumber;
    }
}
