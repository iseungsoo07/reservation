package com.example.reservation.service;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.StoreResponse;

public interface StoreService {
    StoreResponse addStore(Store store);
}
