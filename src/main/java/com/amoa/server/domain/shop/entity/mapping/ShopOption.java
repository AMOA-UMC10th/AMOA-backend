package com.amoa.server.domain.shop.entity.mapping;

import com.amoa.server.domain.reservation.enums.ShopOptionType;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.global.entity.BaseEntity;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(name = "option_type", nullable = false)
    private ShopOptionType optionType;

    @Column(name = "option_name", nullable = false, length = 50)
    private String optionName;

    @Column(name = "option_price", nullable = false)
    private int optionPrice;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "max_quantity", nullable = false)
    @Builder.Default
    private int maxQuantity = 10;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
