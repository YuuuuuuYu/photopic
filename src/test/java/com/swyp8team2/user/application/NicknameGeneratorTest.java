package com.swyp8team2.user.application;

import com.swyp8team2.user.domain.NicknameAdjective;
import com.swyp8team2.user.domain.NicknameAdjectiveRepository;
import com.swyp8team2.user.domain.Role;
import com.swyp8team2.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NicknameGeneratorTest {

    @InjectMocks
    NicknameGenerator nicknameGenerator;

    @Mock
    NicknameAdjectiveRepository nicknameAdjectiveRepository;

    @Test
    @DisplayName("닉네임 생성 테스트")
    void generate() throws Exception {
        //given
        Role role = Role.USER;
        given(nicknameAdjectiveRepository.findRandomNicknameAdjective())
                .willReturn(Optional.of(new NicknameAdjective("호기심 많은")));

        //when
        String nickname = nicknameGenerator.generate(role);

        //then
        Assertions.assertThat(nickname).isEqualTo("호기심 많은 뽀또");
    }

    @Test
    @DisplayName("닉네임 생성 테스트 - 게스트")
    void generate_guest() throws Exception {
        //given
        Role role = Role.GUEST;
        given(nicknameAdjectiveRepository.findRandomNicknameAdjective())
                .willReturn(Optional.of(new NicknameAdjective("호기심 많은")));

        //when
        String nickname = nicknameGenerator.generate(role);

        //then
        Assertions.assertThat(nickname).isEqualTo("호기심 많은 낫또");
    }
}
