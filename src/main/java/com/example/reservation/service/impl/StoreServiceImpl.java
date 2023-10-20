package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    public StoreResponse addStore(Store store) {
        Optional<Store> optionalStore = storeRepository.findByAddressAndOwner(store.getAddress(), store.getOwner());

        if (optionalStore.isPresent()) {
            throw new ReservationException(ErrorCode.ALREADY_EXISTS_STORE);
        }

        Store savedStore = storeRepository.save(store);

        return StoreResponse.of(savedStore);
    }
}
