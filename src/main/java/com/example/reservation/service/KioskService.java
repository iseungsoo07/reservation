package com.example.reservation.service;

import com.example.reservation.domain.model.KioskRequest;

public interface KioskService {
    String confirmVisit(Long reservationId, KioskRequest kioskRequest);
}
