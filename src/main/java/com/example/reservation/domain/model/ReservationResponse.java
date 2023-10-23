package com.example.reservation.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private String storeName;
    private String memberName;
    private String address;
    private String contact;
    private LocalDateTime reservationDate;

}
