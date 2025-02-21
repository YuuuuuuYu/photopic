package com.swyp8team2.auth.presentation;


import com.swyp8team2.auth.application.AuthService;
import com.swyp8team2.auth.application.jwt.JwtService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.auth.presentation.dto.OAuthSignInRequest;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.presentation.CustomHeader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final RefreshTokenCookieGenerator refreshTokenCookieGenerator;
    private final AuthService authService;

    @GetMapping("/oauth2/kakao")
    public ResponseEntity<Void> kakaoOAuth() {
        String requestUrl = authService.getOAuthAuthorizationUrl();
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .header(HttpHeaders.LOCATION, requestUrl)
                .build();
    }

    @PostMapping("/oauth2/code/kakao")
    public ResponseEntity<TokenResponse> kakaoOAuthSignIn(
            @Valid @RequestBody OAuthSignInRequest request,
            HttpServletResponse response
    ) {
        TokenPair tokenPair = authService.oauthSignIn(request.code());
        Cookie cookie = refreshTokenCookieGenerator.createCookie(tokenPair.refreshToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(new TokenResponse(tokenPair.accessToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(
            @CookieValue(name = CustomHeader.CustomCookie.REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (Objects.isNull(refreshToken)) {
            throw new BadRequestException(ErrorCode.INVALID_REFRESH_TOKEN_HEADER);
        }
        TokenPair tokenPair = jwtService.reissue(refreshToken);
        Cookie cookie = refreshTokenCookieGenerator.createCookie(tokenPair.refreshToken());
        response.addCookie(cookie);
        return ResponseEntity.ok(new TokenResponse(tokenPair.accessToken()));
    }
}
