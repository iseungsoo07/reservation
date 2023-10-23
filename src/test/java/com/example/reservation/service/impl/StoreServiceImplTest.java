package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Store;
import com.example.reservation.domain.model.StoreResponse;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static com.example.reservation.exception.ErrorCode.ALREADY_EXISTS_STORE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    StoreRepository storeRepository;

    @InjectMocks
    StoreServiceImpl storeService;

    @Test
    @DisplayName("매장 등록 성공")
    void addStore_success() {
        // given
        Store store = Store.builder()
                .id(1L)
                .owner("김사과")
                .name("동대문 엽기떡볶이 상현점")
                .address("경기도 용인시 수지구 상현동")
                .description("둘이 먹다 하나 죽어도 모를 그 맛!")
                .open(LocalTime.of(9, 0, 0))
                .close(LocalTime.of(22, 0, 0))
                .build();

        given(storeRepository.findByAddressAndOwner(anyString(), anyString()))
                .willReturn(Optional.empty());

        given(storeRepository.save(any()))
                .willReturn(store);

        // when
        StoreResponse storeResponse = storeService.addStore(store);

        // then
        assertEquals("김사과", storeResponse.getOwner());
        assertEquals("동대문 엽기떡볶이 상현점", storeResponse.getName());
        assertEquals("경기도 용인시 수지구 상현동", storeResponse.getAddress());
        assertEquals("09:00", storeResponse.getOpen().toString());
        assertEquals("22:00", storeResponse.getClose().toString());
    }

    @Test
    @DisplayName("매장 등록 실패 - 중복된 매장 정보")
    void addStore_fail_AlreadyExistsStore() {
        // given
        Store store = Store.builder()
                .id(1L)
                .owner("김사과")
                .name("동대문 엽기떡볶이 상현점")
                .address("경기도 용인시 수지구 상현동")
                .description("둘이 먹다 하나 죽어도 모를 그 맛!")
                .open(LocalTime.of(9, 0, 0))
                .close(LocalTime.of(22, 0, 0))
                .build();

        given(storeRepository.findByAddressAndOwner(anyString(), anyString()))
                .willReturn(Optional.of(store));

        // when
        ReservationException exception = assertThrows(ReservationException.class, () -> storeService.addStore(store));

        // then
        assertEquals(ALREADY_EXISTS_STORE, exception.getErrorCode());
    }
}