package com.swyp8team2.user.application;

import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.NicknameAdjective;
import com.swyp8team2.user.domain.NicknameAdjectiveRepository;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserServiceTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NicknameAdjectiveRepository nicknameAdjectiveRepository;

    @Autowired
    UserService userService;

    @Test
    void createUser() {
        // given
        User user = User.create(null, "https://image.com/1");
        // 메인 코드에서는 2개만 수정 후 테스트 진행
        nicknameAdjectiveRepository.save(new NicknameAdjective("호기심 많은 뽀또"));
        nicknameAdjectiveRepository.save(new NicknameAdjective("배려 깊은 뽀또"));

        // when
        Long userId = userService.createUser(user.getNickname(), user.getProfileUrl());
        Optional<User> returnUser = userRepository.findById(userId);

        // when then
        assertAll(
                () -> assertThat(returnUser.get().getNickname()).isNotNull(),
                () -> assertThat(returnUser.get().getNickname()).contains("뽀또")
        );

    }
}