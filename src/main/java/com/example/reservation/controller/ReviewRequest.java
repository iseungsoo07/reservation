package com.example.reservation.controller;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    private String userId;
    private Long storeId;
    private String content;

    @Min(0)
    @Max(5)
    private Double rating;
}
