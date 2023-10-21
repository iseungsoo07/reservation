package com.example.reservation.service.impl;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.model.SignInRequest;
import com.example.reservation.domain.model.SignUpRequest;
import com.example.reservation.domain.model.SignUpResponse;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.exception.ReservationException;
import com.example.reservation.repository.MemberRepository;
import com.example.reservation.type.MemberType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberServiceImpl memberService;

    @Test
    @DisplayName("사용자 인증 성공")
    void loadUserByUsername_success() {
        // given
        Member member = Member.builder()
                .id(1L)
                .userId("apple")
                .name("김사과")
                .phone("010-1111-1111")
                .memberType(MemberType.ROLE_PARTNER)
                .build();

        given(memberRepository.findByUserId(anyString()))
                .willReturn(Optional.of(member));

        // when
        UserDetails userDetails = memberService.loadUserByUsername("apple");

        // then
        assertEquals(MemberType.ROLE_PARTNER.toString(),
                userDetails.getAuthorities()
                        .stream().collect(Collectors.toList())
                        .get(0).toString());
    }

    @Test
    @DisplayName("사용자 인증 실패")
    void loadUserByUsername_fail() {
        // given
        Member member = Member.builder()
                .id(1L)
                .userId("apple")
                .name("김사과")
                .phone("010-1111-1111")
                .memberType(MemberType.ROLE_PARTNER)
                .build();

        given(memberRepository.findByUserId(anyString()))
                .willReturn(Optional.empty());

        // when
        ReservationException exception
                = assertThrows(ReservationException.class, () -> memberService.loadUserByUsername("김사과"));

        // then
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .userId("apple")
                .password("1111")
                .name("김사과")
                .phone("010-1111-1111")
                .memberType(MemberType.ROLE_PARTNER)
                .build();

        given(memberRepository.existsByUserId(anyString()))
                .willReturn(false);

        // when

        SignUpResponse signUpResponse = memberService.signUp(request);

        // then
        assertEquals("김사과", signUpResponse.getName());
        assertEquals("apple", signUpResponse.getUserId());
        assertEquals("010-1111-1111", signUpResponse.getPhone());
        assertEquals(MemberType.ROLE_PARTNER, signUpResponse.getMemberType());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 사용중인 아이디")
    void signup_fail() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .userId("apple")
                .password("1111")
                .name("김사과")
                .phone("010-1111-1111")
                .memberType(MemberType.ROLE_PARTNER)
                .build();

        given(memberRepository.existsByUserId(anyString()))
                .willReturn(true);

        // when
        ReservationException exception
                = assertThrows(ReservationException.class, () -> memberService.signUp(request));

        // then
        assertEquals(ErrorCode.ALREADY_USING_ID, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 성공")
    void authenticate_success() {
        // given
        Member member = Member.builder()
                .id(1L)
                .userId("apple")
                .name("김사과")
                .password("1111")
                .phone("010-1111-1111")
                .memberType(MemberType.ROLE_PARTNER)
                .build();

        SignInRequest request = SignInRequest.builder()
                .userId("apple")
                .password("1111")
                .build();

        given(memberRepository.findByUserId(anyString()))
                .willReturn(Optional.of(member));

        given(passwordEncoder.matches(any(), anyString()))
                .willReturn(true);

        // when
        Member loginMember = memberService.authenticate(request);

        // then
        assertEquals("김사과", loginMember.getName());
        assertEquals("apple", loginMember.getUserId());
        assertEquals("010-1111-1111", loginMember.getPhone());
        assertEquals(MemberType.ROLE_PARTNER, loginMember.getMemberType());
    }
    
     @Test
     @DisplayName("로그인 실패 - 아이디 불일치(사용자 정보 없음)")
     void authenticate_fail_NotFoundMember() {
         // given
         Member member = Member.builder()
                 .id(1L)
                 .userId("apple")
                 .name("김사과")
                 .password("1111")
                 .phone("010-1111-1111")
                 .memberType(MemberType.ROLE_PARTNER)
                 .build();

         SignInRequest request = SignInRequest.builder()
                 .userId("apple")
                 .password("1111")
                 .build();

         given(memberRepository.findByUserId(anyString()))
                 .willReturn(Optional.of(member));
                 
         // when
         ReservationException exception
                 = assertThrows(ReservationException.class, () -> memberService.authenticate(request));

         // then
         assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
     }
     
     @Test
     @DisplayName("로그인 실패 - 비밀번호 불일치")
     void authenticate_fail_PasswordUnmatch() {
         // given
         Member member = Member.builder()
                 .id(1L)
                 .userId("apple")
                 .name("김사과")
                 .password("1111")
                 .phone("010-1111-1111")
                 .memberType(MemberType.ROLE_PARTNER)
                 .build();

         SignInRequest request = SignInRequest.builder()
                 .userId("apple")
                 .password("1111")
                 .build();

         given(memberRepository.findByUserId(anyString()))
                 .willReturn(Optional.of(member));

         given(passwordEncoder.matches(any(), anyString()))
                 .willReturn(false);

         // when
         ReservationException exception
                 = assertThrows(ReservationException.class, () -> memberService.authenticate(request));

         // then
         assertEquals(ErrorCode.PASSWORD_UNMATCH, exception.getErrorCode());
     }
}