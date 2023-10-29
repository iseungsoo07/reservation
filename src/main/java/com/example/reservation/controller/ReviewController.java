package com.example.reservation.controller;

import com.example.reservation.service.ReviewService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{reservationId}")
    public ResponseEntity<?> addReview(@PathVariable Long reservationId, @Valid @RequestBody ReviewRequest reviewRequest) {
        LoginCheckUtils.loginCheck();

        return ResponseEntity.ok(reviewService.addReview(reservationId, reviewRequest));
    }
}
