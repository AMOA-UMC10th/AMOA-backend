package com.amoa.server.domain.user.entity.mapping;

import com.amoa.server.domain.term.entity.Term;
import com.amoa.server.domain.user.entity.User;
import com.amoa.server.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_agreement",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_agreement_user_term_version",
                        columnNames = {
                                "user_id",
                                "term_id",
                                "agreed_version"
                        }
                )
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_agreement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(name = "agreed_version", nullable = false, length = 20)
    private String agreedVersion;

    @Column(name = "is_agreed", nullable = false)
    private boolean isAgreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    public static UserAgreement create(
            User user,
            Term term,
            boolean isAgreed
    ) {
        return UserAgreement.builder()
                .user(user)
                .term(term)
                .agreedVersion(term.getVersion())
                .isAgreed(isAgreed)
                .agreedAt(isAgreed ? LocalDateTime.now() : null)
                .build();
    }
}