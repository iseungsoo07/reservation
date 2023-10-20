package com.example.reservation.domain.model;

import com.example.reservation.domain.entity.Member;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
    private String userId;
    private String password;
    private String userName;
    private String phone;
    private List<String> roles;

    public Member toEntity() {
        return Member.builder()
                .userId(this.userId)
                .password(this.password)
                .userName(this.userName)
                .phone(this.phone)
                .roles(this.roles)
                .build();
    }
}
