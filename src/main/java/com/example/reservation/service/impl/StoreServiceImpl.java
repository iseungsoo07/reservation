package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.StoreRequest;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.StoreService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.NOT_FOUND_STORE;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    public StoreResponse addStore(StoreRequest storeRequest) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();
        String userId = userDetails.getUsername();

        Optional<Store> optionalStore = storeRepository.findByAddressAndContact(storeRequest.getAddress(), storeRequest.getContact());

        if (optionalStore.isPresent()) {
            throw new ReservationException(ErrorCode.ALREADY_EXISTS_STORE);
        }

        Store store = Store.builder()
                .owner(userId)
                .name(storeRequest.getName())
                .address(storeRequest.getAddress())
                .description(storeRequest.getDescription())
                .contact(storeRequest.getContact())
                .open(storeRequest.getOpen())
                .close(storeRequest.getClose())
                .build();

        Store savedStore = storeRepository.save(store);

        return StoreResponse.of(savedStore);
    }

    @Override
    public Page<StoreResponse> getStoresOrderByName(Pageable pageable) {
        Page<Store> orderByName = storeRepository.findAllByOrderByName(pageable);

        return StoreResponse.toDtoList(orderByName);
    }

    @Override
    public Page<StoreResponse> getStoresOrderByRating(Pageable pageable) {
        Page<Store> orderByRating = storeRepository.findAllByOrderByRatingDesc(pageable);

        return StoreResponse.toDtoList(orderByRating);
    }

    @Override
    public Page<StoreResponse> getStoresOrderByReviewCount(Pageable pageable) {
        Page<Store> orderByReviewCount = storeRepository.findAllByOrderByReviewCountDesc(pageable);

        return StoreResponse.toDtoList(orderByReviewCount);
    }

    @Override
    public List<StoreResponse> searchStore(String name) {
        List<Store> storeList = storeRepository.findByNameStartsWith(name);

        return storeList.stream().map(StoreResponse::of).collect(Collectors.toList());
    }

    @Override
    public StoreResponse getStoreDetails(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));

        return StoreResponse.of(store);
    }


}
