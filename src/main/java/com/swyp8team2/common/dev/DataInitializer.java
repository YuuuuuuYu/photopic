package com.swyp8team2.common.dev;

import com.swyp8team2.auth.application.jwt.JwtService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"dev", "local"})
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostConstruct
    public void init() {
        User save = userRepository.save(User.create("nickname", "defailt_profile_image"));
        TokenPair tokenPair = jwtService.createToken(save.getId());
        System.out.println("accessToken = " + tokenPair.accessToken());
        System.out.println("refreshToken = " + tokenPair.refreshToken());
    }
}
