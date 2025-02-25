package com.swyp8team2.post.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
}
