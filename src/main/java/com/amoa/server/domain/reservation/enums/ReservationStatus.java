package com.amoa.server.domain.reservation.enums;

public enum ReservationStatus {
    DRAFT,       // 옵션까지 선택했지만 일정은 미확정
    PENDING,     // 일정 선택 완료, 예약 승인 대기
    CONFIRMED,   // 예약 확정
    CANCELLED    // 예약 취소
}
