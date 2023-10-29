package com.example.reservation.service.impl;

import com.example.reservation.controller.ReviewRequest;
import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.entity.Review;
import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.ReviewResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.ReviewRepository;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.example.reservation.type.ReservationStatus.APPROVAL;
import static com.example.reservation.type.ReservationStatus.REVIEWED;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 로그인 된 회원이 이용내역을 볼 수 있고
     * 그 이용 내역을 볼 수 있는 화면에서 각 내역에 대한 리뷰를 작성할 수 있도록 함
     */
    @Override
    public ReviewResponse addReview(Long reservationId, ReviewRequest reviewRequest) {
        Member member = memberRepository.findByUserId(reviewRequest.getUserId())
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_MEMBER));

        Store store = storeRepository.findById(reviewRequest.getStoreId())
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_STORE));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));

        validateReservation(reviewRequest, reservation);

        Review savedReview = reviewRepository.save(Review.builder()
                .member(member)
                .store(store)
                .content(reviewRequest.getContent())
                .rating(reviewRequest.getRating())
                .reservation(reservation)
                .build());

        store.updateRating(reviewRequest.getRating());
        storeRepository.save(store);

        reservation.updateStatus(REVIEWED);
        reservationRepository.save(reservation);

        return ReviewResponse.fromEntity(savedReview);
    }

    private static void validateReservation(ReviewRequest reviewRequest, Reservation reservation) {
        if (reservation.getReservationStatus() == REVIEWED) {
            throw new ReservationException(ErrorCode.ALREADY_REVIEWED_RESERVATION);
        }

        if (!reservation.isVisitYn() || reservation.getReservationStatus() != APPROVAL) {
            throw new ReservationException(ErrorCode.NOT_USED_RESERVATION);
        }

        if (!Objects.equals(reservation.getMember().getUserId(), reviewRequest.getUserId())
                || !Objects.equals(reservation.getStore().getId(), reviewRequest.getStoreId())) {
            throw new ReservationException(ErrorCode.UNMATCH_RESERVED_INFORMATION);
        }
    }

    @Override
    public List<ReviewResponse> showReviews(Long storeId) {
        return null;
    }
}
