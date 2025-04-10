package com.swyp8team2.user.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.user.domain.Role;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.user.presentation.dto.UserInfoResponse;
import com.swyp8team2.user.presentation.dto.UserMyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;

    @Transactional
    public Long createUser(String nickname, String profileImageUrl) {
        User user = userRepository.save(User.create(getNickname(nickname), getProfileImage(profileImageUrl)));
        return user.getId();
    }

    private String getNickname(String nickname) {
        return Optional.ofNullable(nickname)
                .orElseGet(() -> nicknameGenerator.generate(Role.USER));
    }

    private String getProfileImage(String profileImageUrl) {
        return Optional.ofNullable(profileImageUrl)
                .orElse(User.DEFAULT_PROFILE_URL);
    }

    @Transactional
    public User createGuest() {
        return userRepository.save(User.createGuest(nicknameGenerator.generate(Role.GUEST)));
    }

    public UserInfoResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.of(user);
    }

    public UserMyInfoResponse findByMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserMyInfoResponse.of(user);
    }
}
