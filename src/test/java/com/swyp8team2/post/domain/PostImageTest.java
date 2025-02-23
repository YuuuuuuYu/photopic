package com.swyp8team2.post.domain;

import com.swyp8team2.common.exception.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PostImageTest {

    @Test
    @DisplayName("게시글 이미지 생성")
    void create() throws Exception {
        //given
        String name = "뽀또A";
        long imageFileId = 1L;

        //when
        PostImage postImage = PostImage.create(name, imageFileId);

        //then
        assertAll(
                () -> assertThat(postImage.getName()).isEqualTo(name),
                () -> assertThat(postImage.getImageFileId()).isEqualTo(imageFileId),
                () -> assertThat(postImage.getVoteCount()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("게시글 이미지 생성 - null 값이 들어온 경우")
    void create_null() throws Exception {
        //given

        //when then
        assertAll(
                () -> assertThatThrownBy(() -> PostImage.create(null, 1L))
                        .isInstanceOf(InternalServerException.class),
                () -> assertThatThrownBy(() -> PostImage.create("뽀또A", null))
                        .isInstanceOf(InternalServerException.class)
        );
    }
}
