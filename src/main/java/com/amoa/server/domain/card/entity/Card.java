package com.amoa.server.domain.card.entity;

import com.amoa.server.domain.card.enums.ArtDesign;
import com.amoa.server.domain.card.enums.ArtType;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class Card extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "max_price", nullable = false)
    private Integer maxPrice;

    @Column(name = "min_price", nullable = false)
    private Integer minPrice;

    @Column(name = "art_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtType artType;

    @Column(name = "like_card", nullable = false)
    @Builder.Default
    private Integer likeCard = 0;

    @Column(name = "art_design", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtDesign artDesign;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "created_month")
    private LocalDate createdMonth; // 이달의 아트에서 몇월의 디자인인지를 표현

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ==================== 아트 등록 API 검증 메서드 ====================

    /**
     * 카드의 가격 범위가 유효한지 확인합니다. (minPrice <= maxPrice)
     *
     * @return 가격 범위가 유효하면 true, 아니면 false
     */
    public boolean isPriceRangeValid() {
        if (this.minPrice == null || this.maxPrice == null) {
            return false;
        }
        return this.minPrice <= this.maxPrice;
    }
}
