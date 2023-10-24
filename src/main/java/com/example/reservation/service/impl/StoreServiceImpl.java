package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.domain.model.ReservationResponse;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public StoreResponse addStore(Store store) {
        Optional<Store> optionalStore = storeRepository.findByAddressAndOwner(store.getAddress(), store.getOwner());

        if (optionalStore.isPresent()) {
            throw new ReservationException(ErrorCode.ALREADY_EXISTS_STORE);
        }

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

    @Override
    public ReservationResponse reserveStore(Long id, String userId, ReservationRequest reservationRequest) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));

        Optional<Reservation> reservationOptional
                = reservationRepository.findByReservationDate(reservationRequest.getReservationDate());

        if (reservationOptional.isPresent()) {
            throw new ReservationException(ALREADY_RESERVED_TIME);
        }

        reservationRepository.save(Reservation.builder()
                .member(member)
                .store(store)
                .reservationDate(reservationRequest.getReservationDate())
                .build());

        return ReservationResponse.builder()
                .storeName(store.getName())
                .memberName(member.getName())
                .address(store.getAddress())
                .contact(store.getContact())
                .reservationDate(reservationRequest.getReservationDate())
                .build();
    }

}
