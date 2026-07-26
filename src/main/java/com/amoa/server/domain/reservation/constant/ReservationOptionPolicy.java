package com.amoa.server.domain.reservation.constant;

import java.time.LocalTime;

public final class ReservationOptionPolicy {

    public static final int EXTENSION_REMOVAL_MAX_QUANTITY = 10;

    public static final LocalTime BUSINESS_OPEN_TIME = LocalTime.of(10, 0);

    public static final LocalTime BUSINESS_CLOSE_TIME = LocalTime.of(20, 0);
}
