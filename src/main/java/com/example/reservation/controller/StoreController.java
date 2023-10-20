package com.example.reservation.controller;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.reservation.exception.ErrorCode.ACCESS_DENIED;


@RestController
@RequiredArgsConstructor
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

}
