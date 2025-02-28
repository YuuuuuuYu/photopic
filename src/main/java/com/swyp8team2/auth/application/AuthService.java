package com.swyp8team2.auth.application;

import com.swyp8team2.auth.application.jwt.JwtService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.auth.application.oauth.OAuthService;
import com.swyp8team2.auth.application.oauth.dto.OAuthUserInfo;
import com.swyp8team2.auth.domain.Provider;
import com.swyp8team2.auth.domain.SocialAccount;
import com.swyp8team2.auth.domain.SocialAccountRepository;
import com.swyp8team2.auth.presentation.dto.TokenResponse;
import com.swyp8team2.common.annotation.GuestTokenCryptoService;
import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.user.application.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final OAuthService oAuthService;
    private final SocialAccountRepository socialAccountRepository;
    private final UserService userService;
    private final CryptoService cryptoService;

    public AuthService(
            JwtService jwtService,
            OAuthService oAuthService,
            SocialAccountRepository socialAccountRepository,
            UserService userService,
            @GuestTokenCryptoService CryptoService cryptoService) {
        this.jwtService = jwtService;
        this.oAuthService = oAuthService;
        this.socialAccountRepository = socialAccountRepository;
        this.userService = userService;
        this.cryptoService = cryptoService;
    }

    @Transactional
    public TokenResponse oauthSignIn(String code, String redirectUri) {
        OAuthUserInfo oAuthUserInfo = oAuthService.getUserInfo(code, redirectUri);
        SocialAccount socialAccount = socialAccountRepository.findBySocialIdAndProvider(
                        oAuthUserInfo.socialId(),
                        Provider.KAKAO
                ).orElseGet(() -> createUser(oAuthUserInfo));
        return jwtService.createToken(socialAccount.getUserId());
    }

    private SocialAccount createUser(OAuthUserInfo oAuthUserInfo) {
        Long userId = userService.createUser(oAuthUserInfo.nickname(), oAuthUserInfo.profileImageUrl());
        return socialAccountRepository.save(SocialAccount.create(userId, oAuthUserInfo));
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        return jwtService.reissue(refreshToken);
    }

    @Transactional
    public String createGuestToken() {
        Long guestId = userService.createGuest();
        return cryptoService.encrypt(String.valueOf(guestId));
    }
}
