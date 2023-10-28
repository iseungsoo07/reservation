package com.example.reservation.service;

import com.example.reservation.domain.model.ReservationPartnerResponse;
import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.domain.model.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservationService {
    ReservationResponse reserveStore(Long id, String userId, ReservationRequest reservationRequest);

    Page<ReservationResponse> getReservationListForUser(String userId, Pageable pageable);

    Page<ReservationPartnerResponse> getReservationListForPartner(String userId, Long storeId, LocalDate date, Pageable pageable);

    ReservationPartnerResponse approveReservation(String userId, Long reservationId);

    ReservationPartnerResponse refuseReservation(String userId, Long reservationId);
}
