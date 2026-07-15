package com.amoa.server.domain.shop.entity;

//*임시로 만든 클래스

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @Column(name = "region_id", nullable = false)
    private Long region_id;

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName;

    @Column(name = "kakao_channel_url", length = 500)
    private String kakaoChannelUrl;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "business_hours", length = 255)
    private String businessHours;

    @Builder.Default
    @Column(name = "shop_status", nullable = false, length = 20)
    private String shopStatus = "Draft";

    @Builder.Default
    @Column(name = "like_shop", nullable = false)
    private int likeShop = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
