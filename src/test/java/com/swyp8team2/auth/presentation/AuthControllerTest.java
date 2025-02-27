package com.swyp8team2.auth.presentation;

import com.swyp8team2.auth.application.AuthService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.auth.presentation.dto.GuestTokenResponse;
import com.swyp8team2.auth.presentation.dto.OAuthSignInRequest;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.ErrorResponse;
import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.support.RestDocsTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends RestDocsTest {

    @Autowired
    AuthService authService;

    @Test
    @DisplayName("카카오 로그인 코드로 토큰 발급")
    void kakaoOAuthSignIn() throws Exception {
        //given
        TokenPair expectedTokenPair = new TokenPair("accessToken", "refreshToken");
        TokenResponse response = new TokenResponse(expectedTokenPair.accessToken());
        given(authService.oauthSignIn(anyString(), anyString()))
                .willReturn(expectedTokenPair);
        OAuthSignInRequest request = new OAuthSignInRequest("code", "https://dev.photopic.site");

        //when then
        mockMvc.perform(post("/auth/oauth2/code/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andExpect(cookie().value(CustomHeader.CustomCookie.REFRESH_TOKEN, expectedTokenPair.refreshToken()))
                .andExpect(cookie().httpOnly(CustomHeader.CustomCookie.REFRESH_TOKEN, true))
                .andExpect(cookie().path(CustomHeader.CustomCookie.REFRESH_TOKEN, "/"))
                .andExpect(cookie().secure(CustomHeader.CustomCookie.REFRESH_TOKEN, true))
                .andExpect(cookie().attribute(CustomHeader.CustomCookie.REFRESH_TOKEN, "SameSite", "None"))
                .andExpect(cookie().maxAge(CustomHeader.CustomCookie.REFRESH_TOKEN, 60 * 60 * 24 * 14))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("code").description("카카오 인증 코드"),
                                fieldWithPath("redirectUri").description("카카오 인증 redirect uri")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("액세스 토큰")
                        ),
                        responseCookies(
                                cookieWithName(CustomHeader.CustomCookie.REFRESH_TOKEN).description("리프레시 토큰")
                        )
                ));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("토큰 재발급")
    void reissue() throws Exception {
        //given
        String newRefreshToken = "newRefreshToken";
        given(authService.reissue(anyString()))
                .willReturn(new TokenPair("accessToken", newRefreshToken));
        TokenResponse response = new TokenResponse("accessToken");

        //when then
        mockMvc.perform(post("/auth/reissue")
                        .cookie(new Cookie(CustomHeader.CustomCookie.REFRESH_TOKEN, "refreshToken")))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andExpect(cookie().value(CustomHeader.CustomCookie.REFRESH_TOKEN, newRefreshToken))
                .andExpect(cookie().httpOnly(CustomHeader.CustomCookie.REFRESH_TOKEN, true))
                .andExpect(cookie().path(CustomHeader.CustomCookie.REFRESH_TOKEN, "/"))
                .andExpect(cookie().secure(CustomHeader.CustomCookie.REFRESH_TOKEN, true))
                .andExpect(cookie().attribute(CustomHeader.CustomCookie.REFRESH_TOKEN, "SameSite", "None"))
                .andExpect(cookie().maxAge(CustomHeader.CustomCookie.REFRESH_TOKEN, 60 * 60 * 24 * 14))
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName(CustomHeader.CustomCookie.REFRESH_TOKEN).description("리프레시 토큰")
                        ),
                        responseCookies(
                                cookieWithName(CustomHeader.CustomCookie.REFRESH_TOKEN).description("새 리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("새 액세스 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("토큰 재발급 - 리프레시 토큰 헤더 없는 경우")
    void reissue_invalidRefreshTokenHeader() throws Exception {
        //given
        ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_REFRESH_TOKEN_HEADER);

        //when then
        mockMvc.perform(post("/auth/reissue"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("토큰 재발급 - 리프레시 토큰 헤더가 db에 없는 경우")
    void reissue_refreshTokenNotFound() throws Exception {
        //given
        ErrorResponse response = new ErrorResponse(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        given(authService.reissue(anyString()))
                .willThrow(new BadRequestException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        //when then
        mockMvc.perform(post("/auth/reissue")
                        .cookie(new Cookie(CustomHeader.CustomCookie.REFRESH_TOKEN, "refreshToken")))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("토큰 재발급 - 리프레시 토큰 헤더가 db에 있는 값과 일치하지 않은 경우")
    void reissue_refreshTokenMismatched() throws Exception {
        //given
        ErrorResponse response = new ErrorResponse(ErrorCode.REFRESH_TOKEN_MISMATCHED);
        given(authService.reissue(anyString()))
                .willThrow(new BadRequestException(ErrorCode.REFRESH_TOKEN_MISMATCHED));

        //when then
        mockMvc.perform(post("/auth/reissue")
                        .cookie(new Cookie(CustomHeader.CustomCookie.REFRESH_TOKEN, "refreshToken")))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("게스트 토큰 발급")
    void guestLogin() throws Exception {
        //given
        String guestToken = "guestToken";
        given(authService.guestLogin())
                .willReturn(guestToken);

        //when then
        mockMvc.perform(post("/auth/guest/token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new GuestTokenResponse(guestToken))))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("guestToken").description("게스트 토큰")
                        )
                ));
    }
}
