package com.amoa.server.domain.reservation.enums;

public enum ReservationStatus {
    // 예약 생성(옵션 선택 완료
    DRAFT,
    // 예약 확정(시술 예정)
    RESERVED,
    // 시술 완료
    COMPLETED,
    // 예약 취소
    CANCELED
}
