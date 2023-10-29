package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.model.KioskRequest;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.service.KioskService;
import com.example.reservation.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KioskServiceImpl implements KioskService {

    private final ReservationRepository reservationRepository;

    @Override
    public String confirmVisit(Long reservationId, KioskRequest kioskRequest) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));

        validateReservation(kioskRequest, reservation);

        reservation.updateVisitYn(true);
        reservationRepository.save(reservation);

        return "예왁 확인이 완료되었습니다.";
    }

    private static void validateReservation(KioskRequest kioskRequest, Reservation reservation) {
        if (!Objects.equals(reservation.getMember().getUserId(), kioskRequest.getUserId())
                || !Objects.equals(reservation.getMember().getName(), kioskRequest.getName())
                || !Objects.equals(reservation.getMember().getPhone(), kioskRequest.getPhone())) {
            throw new ReservationException(ErrorCode.UNMATCH_RESERVED_INFORMATION);
        }

        if (reservation.getReservationStatus() != ReservationStatus.APPROVAL) {
            throw new ReservationException(ErrorCode.NOT_APPROVED_RESERVATION);
        }

        if (reservation.isVisitYn()) {
            throw new ReservationException(ErrorCode.ALREADY_VISITED_RESERVATION);
        }

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        if (now.isBefore(reservation.getReservationDate().minusMinutes(10))) {
            throw new ReservationException(ErrorCode.ARRIVE_TOO_EARLY);
        }

        if (now.plusMinutes(10).isAfter(reservation.getReservationDate())) {
            throw new ReservationException(ErrorCode.ARRIVE_TOO_LATE);
        }
    }
}
