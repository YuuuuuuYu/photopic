package com.swyp8team2.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("user Entity 생성")
    void create() throws Exception {
        //given
        String nickname = "nickname";

        //when
        User user = User.create(nickname, "email");

        //then
        assertThat(user.getNickname()).isEqualTo(nickname);
    }
}
