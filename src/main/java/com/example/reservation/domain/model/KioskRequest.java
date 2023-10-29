package com.example.reservation.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KioskRequest {
    private String userId;
    private String name;
    private String phone;
}
