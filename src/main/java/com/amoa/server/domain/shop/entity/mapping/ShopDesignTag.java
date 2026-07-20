package com.amoa.server.domain.shop.entity.mapping;

import com.amoa.server.domain.common.entity.DesignTag;
import com.amoa.server.domain.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_design_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopDesignTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_design_tag_id")
    private Long shopDesignTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "design_tag_id", nullable = false)
    private DesignTag designTag;
}