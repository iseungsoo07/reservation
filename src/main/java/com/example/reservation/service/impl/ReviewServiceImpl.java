package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.entity.Reservation;
import com.example.reservation.domain.entity.Review;
import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.MessageResponse;
import com.example.reservation.domain.model.ReviewRequest;
import com.example.reservation.domain.model.ReviewResponse;
import com.example.reservation.domain.model.ReviewUpdateRequest;
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
import java.util.stream.Collectors;

import static com.example.reservation.exception.ErrorCode.NOT_FOUND_REVIEW;
import static com.example.reservation.exception.ErrorCode.UNMATCH_REVIEW_USER;
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
     * 로그인 된 회원이 이용내역을 볼 수 있고 (마이페이지 느낌)
     * 그 이용 내역을 볼 수 있는 화면에서 각 내역에 대한 리뷰를 작성할 수 있도록 함
     * 한 번 리뷰를 작성한 예약 내역에 대해서는 다시 작성할 수 없도록 한다. (매장 평점 조작 방지)
     */
    @Override
    public ReviewResponse addReview(Long reservationId, ReviewRequest reviewRequest, String userId) {
        Member member = memberRepository.findByUserId(reviewRequest.getUserId())
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_MEMBER));

        Store store = storeRepository.findById(reviewRequest.getStoreId())
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_STORE));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));

        validateReservation(reviewRequest, reservation, userId);

        Review savedReview = reviewRepository.save(Review.builder()
                .member(member)
                .store(store)
                .content(reviewRequest.getContent())
                .rating(reviewRequest.getRating())
                .reservation(reservation)
                .build());

        // 매장의 평점 정보를 업데이트
        store.updateRating();
        storeRepository.save(store);

        // 예약 상태를 리뷰 완료 상태로 변경
        reservation.updateStatus(REVIEWED);
        reservationRepository.save(reservation);

        return ReviewResponse.fromEntity(savedReview);
    }

    /**
     * 입력받은 값에 대한 유효성 검증 로직
     */
    private static void validateReservation(ReviewRequest reviewRequest, Reservation reservation, String userId) {
        if (!Objects.equals(reservation.getMember().getUserId(), userId)) {
            throw new ReservationException(ErrorCode.UNMATCH_RESERVATION_USER);
        }

        // 예약 상태가 리뷰 완료 상태라면 예외 발생
        if (reservation.getReservationStatus() == REVIEWED) {
            throw new ReservationException(ErrorCode.ALREADY_REVIEWED_RESERVATION);
        }

        // 예약 상태가 승인 상태가 아니거나, 방문처리된 상태가 아니면 예외 발생
        if (!reservation.isVisitYn() || reservation.getReservationStatus() != APPROVAL) {
            throw new ReservationException(ErrorCode.NOT_USED_RESERVATION);
        }

        // 예약 정보와 입력받은 사용자 아이디, 매장 아이디가 일치하지 않으면 예외 발생
        if (!Objects.equals(reservation.getMember().getUserId(), reviewRequest.getUserId())
                || !Objects.equals(reservation.getStore().getId(), reviewRequest.getStoreId())) {
            throw new ReservationException(ErrorCode.UNMATCH_RESERVED_INFORMATION);
        }
    }

    /**
     * 리뷰 수정
     * 내용과 평점을 변경할 수 있음
     */
    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest reviewUpdateRequest, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_REVIEW));

        if (!Objects.equals(review.getMember().getUserId(), userId)) {
            throw new ReservationException(UNMATCH_REVIEW_USER);
        }

        review.updateReivew(reviewUpdateRequest.getContent(), reviewUpdateRequest.getRating());

        reviewRepository.save(review);

        review.getStore().updateRating();
        storeRepository.save(review.getStore());

        return ReviewResponse.builder()
                .userId(review.getMember().getUserId())
                .storeName(review.getStore().getName())
                .content(review.getContent())
                .rating(review.getRating())
                .build();
    }

    /**
     * 리뷰 삭제
     */
    @Override
    public MessageResponse deleteReview(Long reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_REVIEW));

        if (!Objects.equals(review.getMember().getUserId(), userId)) {
            throw new ReservationException(UNMATCH_REVIEW_USER);
        }

        reviewRepository.delete(review);
        review.getStore().updateRating();

        storeRepository.save(review.getStore());

        return MessageResponse.builder()
                .message("리뷰 삭제 완료!")
                .build();
    }


    /**
     * 매장 아이디를 통해 매장을 찾고,
     * 해당 매장이 가진 리뷰들을 작성일자가 빠른 순으로 정렬해서 리스트를 보여준다.
     */
    @Override
    public List<ReviewResponse> showReviews(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_STORE));

        List<Review> reviews = store.getReviews();

        return reviews.stream()
                .sorted((x, y) -> y.getCreatedAt().compareTo(x.getCreatedAt()))
                .map(review -> ReviewResponse.builder()
                        .userId(review.getMember().getUserId())
                        .storeName(review.getStore().getName())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .build()).collect(Collectors.toList());
    }
}
