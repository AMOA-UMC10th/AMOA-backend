package com.amoa.server.domain.card.entity;

import com.amoa.server.domain.common.enums.ArtType;
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
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "max_price", nullable = false)
    private Integer maxPrice;

    @Column(name = "min_price", nullable = false)
    private Integer minPrice;

    //예약에서의 시간계산을 위한 아트 소요 시간
    //일단 디폴트로 1시간 부여
    @Column(name = "duration_minutes", nullable = false)
    @Builder.Default
    private Integer durationMinutes = 60;

    @Column(name = "art_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtType artType;

    @Column(name = "like_card", nullable = false)
    @Builder.Default
    private Integer likeCard = 0;

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
