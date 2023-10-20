package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.model.SignInRequest;
import com.example.reservation.domain.model.SignUpRequest;
import com.example.reservation.domain.model.SignUpResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.reservation.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.reservation.exception.ErrorCode.PASSWORD_UNMATCH;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUserId(username)
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));
    }

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        boolean exists = memberRepository.existsByUserId(signUpRequest.getUserId());

        if (exists) {
            throw new ReservationException(ErrorCode.ALREADY_USING_ID);
        }

        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        memberRepository.save(signUpRequest.toEntity());

        return SignUpResponse.builder()
                .userId(signUpRequest.getUserId())
                .password(signUpRequest.getPassword())
                .userName(signUpRequest.getUserName())
                .phone(signUpRequest.getPhone())
                .memberType(signUpRequest.getMemberType())
                .build();
    }

    @Override
    public Member authenticate(SignInRequest signInRequest) {
        Member member = memberRepository.findByUserId(signInRequest.getUserId())
                .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(signInRequest.getPassword(), member.getPassword())) {
            throw new ReservationException(PASSWORD_UNMATCH);
        }

        return member;
    }
}
