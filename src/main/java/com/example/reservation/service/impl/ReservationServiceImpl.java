package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.ReservationPartnerResponse;
import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.domain.model.ReservationResponse;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.*;
import static com.example.reservation.type.ReservationStatus.WAITING;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    @Override
    public ReservationResponse reserveStore(Long id, String userId, ReservationRequest reservationRequest) {
        Member member = getMember(userId);
        Store store = getStore(id);

        Optional<Reservation> reservationOptional
                = reservationRepository.findByStoreAndReservationDate(store, reservationRequest.getReservationDate());

        if (reservationOptional.isPresent()) {
            throw new ReservationException(ALREADY_RESERVED_TIME);
        }

        reservationRepository.save(Reservation.builder()
                .member(member)
                .store(store)
                .reservationDate(reservationRequest.getReservationDate())
                .reservationStatus(WAITING)
                .build());

        return ReservationResponse.builder()
                .storeName(store.getName())
                .memberName(member.getName())
                .address(store.getAddress())
                .contact(store.getContact())
                .reservationDate(reservationRequest.getReservationDate())
                .reservationStatus(WAITING)
                .build();
    }

    @Override
    public Page<ReservationResponse> getReservationListForUser(String userId, Pageable pageable) {
        Member member = getMember(userId);

        Page<Reservation> memberReservationPage = reservationRepository.findByMember(member, pageable);

        return ReservationResponse.toDtoList(memberReservationPage);
    }

    @Override
    public Page<ReservationPartnerResponse> getReservationListForPartner
            (String userId, Long storeId, LocalDate date, Pageable pageable) {

        Store store = getStore(storeId);
        Member member = getMember(userId);

        if (!Objects.equals(store.getOwner(), member.getUserId())) {
            throw new ReservationException(CAN_CHECK_OWN_STORE);
        }

        Page<Reservation> storeReservationPage = reservationRepository.findByStoreOrderByReservationDate(store, pageable);

        List<Reservation> filteredReservations = storeReservationPage.stream().filter(
                srp -> srp.getReservationDate().getYear() == date.getYear() &&
                        srp.getReservationDate().getMonth() == date.getMonth() &&
                        srp.getReservationDate().getDayOfMonth() == date.getDayOfMonth()).collect(Collectors.toList());

        Page<Reservation> filteredPage = new PageImpl<>(filteredReservations, pageable, filteredReservations.size());

        return ReservationPartnerResponse.toDtoList(filteredPage);
    }


    private Member getMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));
    }

    private Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));
    }

}
