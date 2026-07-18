package com.amoa.server.domain.reservation.entity;

import com.amoa.server.domain.card.entity.Card;
import com.amoa.server.domain.reservation.enums.GelRemovalType;
import com.amoa.server.domain.reservation.enums.HandState;
import com.amoa.server.domain.reservation.enums.PaymentMethod;
import com.amoa.server.domain.reservation.enums.PaymentStatus;
import com.amoa.server.domain.reservation.enums.ReservationStatus;
import com.amoa.server.domain.shop.entity.Shop;
import com.amoa.server.domain.user.entity.User;
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
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "reservation_number", nullable = false, unique = true)
    private String reservationNumber;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "reservation_start_time", nullable = false)
    private LocalTime reservationStartTime;

    @Column(name = "reservation_end_time", nullable = false)
    private LocalTime reservationEndTime;

    @Column(name = "customer_name", nullable = false, length = 20)
    private String customerName;

    @Column(name = "customer_phone_number", nullable = false, length = 20)
    private String customerPhoneNumber;

    @Column(name = "request_message", length = 500)
    private String requestMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "hand_state", nullable = false)
    private HandState handState;

    @Enumerated(EnumType.STRING)
    @Column(name = "gel_removal_type")
    private GelRemovalType gelRemovalType;

    @Column(name = "extension_removal_count", nullable = false)
    private int extensionRemovalCount;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "deposit_amount", nullable = false)
    private int depositAmount;

    @Column(name = "total_duration_minutes", nullable = false)
    private int totalDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus reservationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "is_refund_policy_agreed", nullable = false)
    private boolean isRefundPolicyAgreed;
}
