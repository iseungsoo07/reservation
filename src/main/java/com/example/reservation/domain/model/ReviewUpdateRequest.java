package com.example.reservation.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateRequest {
    private String content;
    private Double rating;
}
