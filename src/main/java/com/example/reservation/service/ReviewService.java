package com.example.reservation.service;

import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.ReviewRequest;
import com.example.reservation.domain.model.ReviewResponse;
import com.example.reservation.domain.model.ReviewUpdateRequest;

import java.util.List;

public interface ReviewService {
    ReviewResponse addReview(Long storeId, ReviewRequest reviewRequest, String userId);

    ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest, String userId);

    MessageResponse deleteReview(Long reviewId, String userId);

    List<ReviewResponse> showReviews(Long storeId);

}
