package com.example.reservation.domain.model;

import com.example.reservation.domain.entity.Store;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse {
    private String owner;
    private String name;
    private String address;
    private String description;
    private String contact;
    private LocalTime open;
    private LocalTime close;

    public static StoreResponse of(Store store) {
        return StoreResponse.builder()
                .owner(store.getOwner())
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .contact(store.getContact())
                .open(store.getOpen())
                .close(store.getClose())
                .build();
    }

    public static Page<StoreResponse> toDtoList(Page<Store> page) {
        return page.map(store -> StoreResponse.builder()
                .owner(store.getOwner())
                .name(store.getName())
                .address(store.getAddress())
                .contact(store.getContact())
                .description(store.getDescription())
                .open(store.getOpen())
                .close(store.getClose())
                .build());

    }
}
