package com.example.reservation.controller;

import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.service.ReservationService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.ONLY_FOR_PARTNER;
import static com.example.reservation.exception.ErrorCode.ONLY_FOR_USER;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 매장 예약
     */
    @PostMapping("/{storeId}")
    public ResponseEntity<?> reserveStore(@PathVariable Long storeId,
                                          @RequestBody ReservationRequest reservationRequest) {

        UserDetails userDetails = LoginCheckUtils.loginCheck();
        String userId = userDetails.getUsername();

        return ResponseEntity.ok(reservationService.reserveStore(storeId, userId, reservationRequest));
    }

    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    /**
     * 예약 확인 - 사용자
     * 자기가 예약한 예약 내역을 리스트로 보여준다.
     * 로그인한 사용자가 유저 권한을 가지고 있어야 함.
     */
    @GetMapping("/list")
    public ResponseEntity<?> getReservationListForUser(Pageable pageable) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        List<String> authorities = getAuthorities(userDetails);

        String userId = userDetails.getUsername();

        if (!authorities.contains("ROLE_USER")) {
            throw new ReservationException(ONLY_FOR_USER);
        }

        return ResponseEntity.ok(reservationService.getReservationListForUser(userId, pageable));
    }

    /**
     * 예약 확인 - 파트너
     * 자기가 관리하는 매장의 예약 현황을 리스트로 보여준다.
     * 로그인한 사용자가 파트너 권한을 가지고 있어야 함.
     */
    @GetMapping("/list/{storeId}")
    public ResponseEntity<?> getReservationListForPartner(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                          @PathVariable Long storeId,
                                                          Pageable pageable) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        List<String> authorities = getAuthorities(userDetails);

        String userId = userDetails.getUsername();

        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        return ResponseEntity.ok(reservationService.getReservationListForPartner(userId, storeId, date, pageable));
    }

    /**
     * 예약 승인
     * 점장이 예약내역을 보고 승인이 가능한 시간대라면 예약을 승인한다.
     * 로그인한 사용자가 파트너 권한을 가지고 있어야 함.
     */
    @PatchMapping("/approval/{reservationId}")
    public ResponseEntity<?> approveReservation(@PathVariable Long reservationId) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        List<String> authorities = getAuthorities(userDetails);

        String userId = userDetails.getUsername();

        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        return ResponseEntity.ok(reservationService.approveReservation(userId, reservationId));
    }

    /**
     * 예약 거절
     * 점장이 예약내역을 보고 승인이 불가능한 시간대라면 예약을 거절한다.
     * 로그인한 사용자가 파트너 권한을 가지고 있어야 함.
     */
    @PatchMapping("/refusal/{reservationId}")
    public ResponseEntity<?> refuseReservation(@PathVariable Long reservationId) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        List<String> authorities = getAuthorities(userDetails);

        String userId = userDetails.getUsername();

        if (!authorities.contains("ROLE_PARTNER")) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        return ResponseEntity.ok(reservationService.refuseReservation(userId, reservationId));
    }

    private static List<String> getAuthorities(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
