package com.swyp8team2.auth.presentation;


import com.swyp8team2.auth.application.AuthService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.auth.presentation.dto.GuestTokenResponse;
import com.swyp8team2.auth.presentation.dto.OAuthSignInRequest;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.auth.presentation.dto.AuthResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.presentation.CustomHeader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final RefreshTokenCookieGenerator refreshTokenCookieGenerator;
    private final AuthService authService;

    @PostMapping("/oauth2/code/kakao")
    public ResponseEntity<AuthResponse> kakaoOAuthSignIn(
            @Valid @RequestBody OAuthSignInRequest request,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authService.oauthSignIn(request.code(), request.redirectUri());
        TokenPair tokenPair = tokenResponse.tokenPair();
        Cookie cookie = refreshTokenCookieGenerator.createCookie(tokenPair.refreshToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenResponse.userId()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> reissue(
            @CookieValue(name = CustomHeader.CustomCookie.REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (Objects.isNull(refreshToken)) {
            throw new BadRequestException(ErrorCode.INVALID_REFRESH_TOKEN_HEADER);
        }
        TokenResponse tokenResponse = authService.reissue(refreshToken);
        TokenPair tokenPair = tokenResponse.tokenPair();
        Cookie cookie = refreshTokenCookieGenerator.createCookie(tokenPair.refreshToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenResponse.userId()));
    }

    @PostMapping("/guest/token")
    public ResponseEntity<GuestTokenResponse> guestToken() {
        String guestToken = authService.createGuestToken();
        return ResponseEntity.ok(new GuestTokenResponse(guestToken));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(
            @CookieValue(name = CustomHeader.CustomCookie.REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        if (Objects.isNull(refreshToken)) {
            throw new BadRequestException(ErrorCode.INVALID_REFRESH_TOKEN_HEADER);
        }
        refreshTokenCookieGenerator.removeCookie(response);
        authService.signOut(userInfo.userId(), refreshToken);
        return ResponseEntity.ok().build();
    }
}
