package com.example.reservation.controller;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.reservation.exception.ErrorCode.ACCESS_DENIED;
import static com.example.reservation.exception.ErrorCode.INVALID_REQUEST;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/regist")
    public ResponseEntity<?> addStore(@RequestBody Store store) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PARTNER"))) {
            throw new ReservationException(ACCESS_DENIED);
        }

        StoreResponse storeResponse = storeService.addStore(store);

        return ResponseEntity.ok(storeResponse);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getStores(@RequestParam(required = false, defaultValue = "name") String orderBy,
                                       Pageable pageable) {
        if ("name".equals(orderBy)) {
            return ResponseEntity.ok(storeService.getStoresOrderByName(pageable));
        }

        if ("rating".equals(orderBy)) {
            return ResponseEntity.ok(storeService.getStoresOrderByRating(pageable));
        }

        if ("review".equals(orderBy)) {
            return ResponseEntity.ok(storeService.getStoresOrderByReviewCount(pageable));
        }

        throw new ReservationException(INVALID_REQUEST);
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
