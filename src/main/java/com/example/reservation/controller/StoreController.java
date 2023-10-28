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

import static com.example.reservation.exception.ErrorCode.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/regist")
    public ResponseEntity<?> addStore(@RequestBody StoreRequest storeRequest) {
        UserDetails userDetails = LoginCheckUtils.loginCheck();

        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PARTNER"))) {
            throw new ReservationException(ONLY_FOR_PARTNER);
        }

        StoreResponse storeResponse = storeService.addStore(storeRequest);

        return ResponseEntity.ok(storeResponse);
    }

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

    @GetMapping("/search")
    public ResponseEntity<?> searchStore(@RequestParam String name) {
        List<StoreResponse> searchResponseList = storeService.searchStore(name);

        return ResponseEntity.ok(searchResponseList);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getStoreDetails(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreDetails(id));
    }


}
