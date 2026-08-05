package com.amoa.server.domain.shop.entity;

import com.amoa.server.domain.common.entity.Region;
import com.amoa.server.domain.shop.entity.mapping.ShopDesignTag;
import com.amoa.server.domain.shop.enums.ShopStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "shop",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_shop_address", columnNames = "address")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName;

    @Column(name = "kakao_channel_url", nullable = false, length = 500)
    private String kakaoChannelUrl;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "legal_code", length = 20)
    private String legalCode;

    @Column(name = "shop_phone_number", length = 20)
    private String shopPhoneNumber;

    @Column(name = "business_hours", length = 255)
    private String businessHours;

    @Column(name = "deposit_amount", nullable = false)
    private int depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_status", nullable = false, length = 20)
    @Builder.Default
    private ShopStatus shopStatus = ShopStatus.DRAFT;

    @Column(name = "like_shop", nullable = false)
    @Builder.Default
    private int likeShop = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ShopDesignTag> shopDesignTags = new ArrayList<>();
}