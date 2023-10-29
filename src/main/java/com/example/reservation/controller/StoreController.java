package com.example.reservation.controller;

import com.example.reservation.domain.model.StoreRequest;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.service.StoreService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.reservation.exception.ErrorCode.INVALID_REQUEST;
import static com.example.reservation.exception.ErrorCode.ONLY_FOR_PARTNER;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    /**
     * 매장 등록
     * 로그인한 사용자가 파트너 권한을 가지고 있어야 한다.
     */
    @PostMapping("/regist")
    public ResponseEntity<?> addStore(@RequestBody StoreRequest storeRequest) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PARTNER"))) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        StoreResponse storeResponse = storeService.addStore(storeRequest);

        return ResponseEntity.ok(storeResponse);
    }

    /**
     * 모든 매장 정보를 가져온다.
     * 쿼리 파라미터로 orderBy 를 입력받아 입력받은 값을 기준으로 정렬한다.
     */
    @GetMapping("/list")
    public ResponseEntity<?> getStores(@RequestParam(required = false, defaultValue = "name") String orderBy,
                                       Pageable pageable) {
        return getStoresByQueryParam(orderBy, pageable);
    }

    private ResponseEntity<?> getStoresByQueryParam(String query, Pageable pageable) {
        switch (query) {
            case "name":
                return ResponseEntity.ok(storeService.getStoresOrderByName(pageable));
            case "rating":
                return ResponseEntity.ok(storeService.getStoresOrderByRating(pageable));
            case "review":
                return ResponseEntity.ok(storeService.getStoresOrderByReviewCount(pageable));
            default:
                throw new ReservationException(INVALID_REQUEST);
        }
    }

    /**
     * 매장 이름을 통해 검색하는 기능
     * 자동완성 기능으로 사용할 수도 있다.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchStore(@RequestParam String name) {
        List<StoreResponse> searchResponseList = storeService.searchStore(name);

        return ResponseEntity.ok(searchResponseList);
    }

    /**
     * 매장 상세정보를 보여준다.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getStoreDetails(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreDetails(id));
    }


}
