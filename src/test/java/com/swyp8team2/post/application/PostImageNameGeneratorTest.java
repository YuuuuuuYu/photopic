package com.swyp8team2.post.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostImageNameGeneratorTest {

    PostImageNameGenerator postImageNameGenerator;

    @BeforeEach
    void setUp() {
        postImageNameGenerator = new PostImageNameGenerator();
    }

    @Test
    @DisplayName("이미지 이름 생성")
    void generate() throws Exception {
        //given

        //when
        String generate1 = postImageNameGenerator.generate();
        String generate2 = postImageNameGenerator.generate();

        //then
        assertThat(generate1).isEqualTo("뽀또A");
        assertThat(generate2).isEqualTo("뽀또B");
    }
}
