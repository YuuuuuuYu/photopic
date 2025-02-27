package com.swyp8team2.user.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.user.domain.NicknameAdjective;
import com.swyp8team2.user.domain.NicknameAdjectiveRepository;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.user.presentation.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;

    @Transactional
    public Long createUser(String nickname, String profileImageUrl) {
        User user = userRepository.save(User.create(getNickname(nickname), getProfileImage(profileImageUrl)));
        return user.getId();
    }

    private String getProfileImage(String profileImageUrl) {
        return Optional.ofNullable(profileImageUrl)
                .orElse("defailt_profile_image");
    }

    private String getNickname(String nickname) {
        return Optional.ofNullable(nickname)
                .orElseGet(() -> {
                    long randomIndex = (long)(Math.random() * 500);
                    Optional<NicknameAdjective> adjective = nicknameAdjectiveRepository.findNicknameAdjectiveById(randomIndex);
                    return adjective.map(NicknameAdjective::getAdjective)
                            .orElse("user_" + System.currentTimeMillis());
                });
    }

    @Transactional
    public Long createGuest() {
        User user = userRepository.save(User.createGuest());
        return user.getId();
    }

    public UserInfoResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.of(user);
    }
}
