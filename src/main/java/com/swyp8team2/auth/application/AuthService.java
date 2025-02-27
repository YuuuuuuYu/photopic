package com.swyp8team2.auth.application;

import com.swyp8team2.auth.application.jwt.JwtService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.auth.application.oauth.OAuthService;
import com.swyp8team2.auth.application.oauth.dto.OAuthUserInfo;
import com.swyp8team2.auth.domain.Provider;
import com.swyp8team2.auth.domain.SocialAccount;
import com.swyp8team2.auth.domain.SocialAccountRepository;
import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final OAuthService oAuthService;
    private final SocialAccountRepository socialAccountRepository;
    private final UserService userService;
    private final CryptoService cryptoService;

    @Transactional
    public TokenPair oauthSignIn(String code, String redirectUri) {
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

    public TokenPair reissue(String refreshToken) {
        return jwtService.reissue(refreshToken);
    }

    public String guestLogin() {
        Long guestId = userService.createGuest();
        return cryptoService.encrypt(String.valueOf(guestId));
    }
}
