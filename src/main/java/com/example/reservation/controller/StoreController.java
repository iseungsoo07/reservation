package com.example.reservation.controller;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.ReservationRequest;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.service.StoreService;
import com.example.reservation.utils.LoginCheckUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.reservation.exception.ErrorCode.ACCESS_DENIED;
import static com.example.reservation.exception.ErrorCode.NEED_LOGIN;


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

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

    @PostMapping("/reservation/{id}")
    public ResponseEntity<?> reserveStore(@PathVariable Long id,
                                          @RequestBody ReservationRequest reservationRequest) {

        UserDetails userDetails = LoginCheckUtils.loginCheck();

        String userId = userDetails.getUsername();

        if (userDetails.getAuthorities()
                .stream().noneMatch(a -> a.getAuthority().equals("ROLE_USER") || a.getAuthority().equals("ROLE_PARTNER"))) {
            throw new ReservationException(NEED_LOGIN);
        }

        log.info("userId = {}", userId);
        return ResponseEntity.ok(storeService.reserveStore(id, userId, reservationRequest));
    }
}
