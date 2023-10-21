package com.example.reservation.controller;

import com.example.reservation.domain.entity.Member;
import com.example.reservation.domain.model.SignInRequest;
import com.example.reservation.domain.model.SignUpRequest;
import com.example.reservation.domain.model.SignUpResponse;
import com.example.reservation.security.TokenProvider;
import com.example.reservation.service.MemberService;
import com.example.reservation.type.MemberType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @MockBean
    TokenProvider tokenProvider;

    @Test
    @DisplayName("회원가입 - 파트너")
    void signup_partner() throws Exception {
        // given
        SignUpResponse member = SignUpResponse.builder()
                .userId("apple")
                .password("1111")
                .name("김사과")
                .phone("010-1111-1111")
                .memberType(MemberType.ROLE_PARTNER)
                .build();

        given(memberService.signUp(any()))
                .willReturn(member);

        // when
        // then
        mockMvc.perform(
                        post("/user/signup")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new SignUpRequest("apple", "1111", "김사과", "010-1111-1111", MemberType.ROLE_PARTNER)
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("apple"))
                .andExpect(jsonPath("$.userName").value("김사과"))
                .andExpect(jsonPath("$.phone").value("010-1111-1111"))
                .andExpect(jsonPath("$.memberType").value("ROLE_PARTNER"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 - 유저")
    void signup_user() throws Exception {
        // given
        SignUpResponse member = SignUpResponse.builder()
                .userId("banana")
                .password("2222")
                .name("반하나")
                .phone("010-2222-2222")
                .memberType(MemberType.ROLE_USER)
                .build();

        given(memberService.signUp(any()))
                .willReturn(member);

        // when
        // then
        mockMvc.perform(
                        post("/user/signup")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new SignUpRequest("banana", "2222", "반하나", "010-2222-2222", MemberType.ROLE_USER)
                                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("banana"))
                .andExpect(jsonPath("$.name").value("반하나"))
                .andExpect(jsonPath("$.phone").value("010-2222-2222"))
                .andExpect(jsonPath("$.memberType").value("ROLE_USER"))
                .andDo(print());
    }

    @Test
    void signin() throws Exception {
        // given
        Member member = Member.builder()
                .id(2L)
                .userId("banana")
                .password("2222")
                .name("반하나")
                .phone("010-2222-2222")
                .memberType(MemberType.ROLE_USER)
                .build();

        given(memberService.authenticate(any()))
                .willReturn(member);

        given(tokenProvider.generateToken(anyString(), any()))
                .willReturn("zerobase_reservation_token_example");

        // when
        // then
        mockMvc.perform(
                        post("/user/signin")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        new SignInRequest("banana", "2222")
                                ))
                ).andExpect(status().isOk())
                .andExpect(content().string("zerobase_reservation_token_example"))
                .andDo(print());

    }
}