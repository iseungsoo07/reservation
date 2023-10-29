package com.example.reservation.controller;

import com.example.reservation.domain.model.ReviewRequest;
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

    /**
     * 예약내역에 대한 리뷰를 작성
     * 로그인하지 않은 경우 리뷰를 작성할 수 없음.
     */
    @PostMapping("/{reservationId}")
    public ResponseEntity<?> addReview(@PathVariable Long reservationId, @Valid @RequestBody ReviewRequest reviewRequest) {
        LoginCheckUtils.loginCheck();

        return ResponseEntity.ok(reviewService.addReview(reservationId, reviewRequest));
    }

    /**
     * 매장에 대한 리뷰 리스트를 보여준다.
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<?> showReviews(@PathVariable Long storeId) {
        return ResponseEntity.ok(reviewService.showReviews(storeId));
    }
}
