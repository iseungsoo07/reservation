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

    @PostMapping("/{storeId}")
    public ResponseEntity<?> reserveStore(@PathVariable Long storeId,
                                          @RequestBody ReservationRequest reservationRequest) {

        UserDetails userDetails = LoginCheckUtils.loginCheck();
        String userId = userDetails.getUsername();

        return ResponseEntity.ok(reservationService.reserveStore(storeId, userId, reservationRequest));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getReservationListForUser(Pageable pageable) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        if (authorities.contains("ROLE_USER")) {
            String userId = userDetails.getUsername();
            return ResponseEntity.ok(reservationService.getReservationListForUser(userId, pageable));
        }

        throw new ReservationException(ONLY_FOR_USER);
    }

    @GetMapping("/list/{storeId}")
    public ResponseEntity<?> getReservationListForPartner(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                          @PathVariable Long storeId,
                                                          Pageable pageable) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String userId = userDetails.getUsername();

        if (authorities.contains("ROLE_PARTNER")) {
            return ResponseEntity.ok(reservationService.getReservationListForPartner(userId, storeId, date, pageable));
        }

        throw new ReservationException(ONLY_FOR_PARTNER);
    }
}
