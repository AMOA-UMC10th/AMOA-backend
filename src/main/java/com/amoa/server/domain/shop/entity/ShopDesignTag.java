package com.amoa.server.domain.shop.entity;

import com.amoa.server.domain.common.entity.DesignTag;
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
    @Column(name = "shop_designtag_id")
    private Long shopDesignTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designtag_id", nullable = false)
    private DesignTag designTag;
}