package com.example.reservation.service;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.domain.model.ReservationResponse;
import com.example.reservation.domain.model.StoreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreService {
    StoreResponse addStore(Store store);

    Page<StoreResponse> getStoresOrderByName(Pageable pageable);

    Page<StoreResponse> getStoresOrderByRating(Pageable pageable);

    Page<StoreResponse> getStoresOrderByReviewCount(Pageable pageable);

    List<StoreResponse> searchStore(String prefix);

    StoreResponse getStoreDetails(Long id);

    ReservationResponse reserveStore(Long id, String userId, ReservationRequest reservationRequest);

}
