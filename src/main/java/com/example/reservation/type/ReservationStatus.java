package com.example.reservation.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReservationStatus {
    APPROVAL("예약 승인"),
    WAITING("승인 대기"),
    REFUSAL("예약 거절");

    private final String description;
}
