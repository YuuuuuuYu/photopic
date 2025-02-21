package com.swyp8team2.user.application;

import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long createUser(String nickname, String profileImageUrl) {
        User user = userRepository.save(User.create(getNickname(nickname), getProfileImage(profileImageUrl)));
        return user.getId();
    }

    private String getProfileImage(String profileImageUrl) {
        return Optional.ofNullable(profileImageUrl)
                .orElse("defailt_profile_image");
    }

    private String getNickname(String email) {
        return Optional.ofNullable(email)
                .orElseGet(() -> "user_" + System.currentTimeMillis());
    }
}
