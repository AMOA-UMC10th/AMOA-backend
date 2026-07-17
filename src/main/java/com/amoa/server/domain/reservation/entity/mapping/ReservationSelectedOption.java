package com.amoa.server.domain.reservation.entity.mapping;

import com.amoa.server.domain.reservation.entity.Reservation;
import com.amoa.server.domain.shop.entity.mapping.ShopOption;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "reservation_selected_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSelectedOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_selected_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_option_id", nullable = false)
    private ShopOption shopOption;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "option_price", nullable = false)
    private int optionPrice;

    @Column(name = "option_total_price", nullable = false)
    private int optionTotalPrice;
}
