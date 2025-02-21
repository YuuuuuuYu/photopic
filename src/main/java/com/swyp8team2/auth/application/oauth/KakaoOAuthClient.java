package com.swyp8team2.auth.application.oauth;

import com.swyp8team2.auth.application.oauth.dto.KakaoAuthResponse;
import com.swyp8team2.auth.application.oauth.dto.KakaoUserInfoResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public interface KakaoOAuthClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoAuthResponse fetchToken(@RequestParam("params") MultiValueMap<String, String> params);

    @GetExchange("https://kapi.kakao.com/v2/user/me")
    KakaoUserInfoResponse fetchUserInfo(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}
