package com.amoa.server.domain.shop.entity.mapping;

import com.amoa.server.domain.shop.entity.Shop;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "shop_option",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shop_option_shop_name",
                        columnNames = {"shop_id", "option_name"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShopOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "option_name", nullable = false, length = 50)
    private String optionName;

    @Column(name = "option_price", nullable = false)
    private int optionPrice;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "max_quantity", nullable = false)
    private int maxQuantity = 10;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Builder
    public ShopOption(
            Shop shop,
            String optionName,
            int optionPrice,
            int durationMinutes,
            int maxQuantity,
            boolean isActive
    ) {
        this.shop = shop;
        this.optionName = optionName;
        this.optionPrice = optionPrice;
        this.durationMinutes = durationMinutes;
        this.maxQuantity = maxQuantity;
        this.isActive = isActive;
    }

    public void update(
            String optionName,
            int optionPrice,
            int durationMinutes,
            int maxQuantity
    ) {
        this.optionName = optionName;
        this.optionPrice = optionPrice;
        this.durationMinutes = durationMinutes;
        this.maxQuantity = maxQuantity;
    }
}
