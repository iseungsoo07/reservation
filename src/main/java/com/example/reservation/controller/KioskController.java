package com.example.reservation.controller;

import com.example.reservation.domain.model.KioskRequest;
import com.example.reservation.service.KioskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kiosk")
public class KioskController {

    private final KioskService kioskService;

    @PatchMapping("/confirm/{reservationId}")
    public ResponseEntity<String> confirmVisit(@PathVariable Long reservationId, @RequestBody KioskRequest kioskRequest) {
        return ResponseEntity.ok(kioskService.confirmVisit(reservationId, kioskRequest));
    }

}