package com.example.reservation.service;

import com.example.reservation.controller.ReviewRequest;
import com.example.reservation.domain.model.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(Long storeId, ReviewRequest reviewRequest);

    List<ReviewResponse> showReviews(Long storeId);
}
