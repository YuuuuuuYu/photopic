package com.swyp8team2.post.domain;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    @DisplayName("게시글 생성")
    void create() throws Exception {
        //given
        long userId = 1L;
        String description = "description";
        List<PostImage> postImages = List.of(
                PostImage.create("뽀또A", 1L),
                PostImage.create("뽀또B", 2L)
        );
        String shareUrl = "shareUrl";

        //when
        Post post = Post.create(userId, description, postImages, shareUrl);

        //then
        List<PostImage> images = post.getImages();
        assertAll(
                () -> assertThat(post.getUserId()).isEqualTo(userId),
                () -> assertThat(post.getDescription()).isEqualTo(description),
                () -> assertThat(post.getShareUrl()).isEqualTo(shareUrl),
                () -> assertThat(post.getState()).isEqualTo(State.PROGRESS),
                () -> assertThat(images).hasSize(2),
                () -> assertThat(images.get(0).getName()).isEqualTo("뽀또A"),
                () -> assertThat(images.get(0).getImageFileId()).isEqualTo(1L),
                () -> assertThat(images.get(0).getVoteCount()).isEqualTo(0),
                () -> assertThat(images.get(1).getName()).isEqualTo("뽀또B"),
                () -> assertThat(images.get(1).getImageFileId()).isEqualTo(2L),
                () -> assertThat(images.get(1).getVoteCount()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("게시글 생성 - 이미지가 2개 미만인 경우")
    void create_invalidPostImageCount() throws Exception {
        //given
        List<PostImage> postImages = List.of(
                PostImage.create("뽀또A", 1L)
        );

        //when then
        assertThatThrownBy(() -> Post.create(1L, "description", postImages, "shareUrl"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_POST_IMAGE_COUNT.getMessage());
    }

    @Test
    @DisplayName("게시글 생성 - 설명이 100자 넘어가는 경우")
    void create_descriptionCountExceeded() throws Exception {
        //given
        String description = "a".repeat(101);
        List<PostImage> postImages = List.of(
                PostImage.create("뽀또A", 1L),
                PostImage.create("뽀또B", 2L)
        );

        //when then
        assertThatThrownBy(() -> Post.create(1L, description, postImages, "shareUrl"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("게시글 생성 - null 값이 들어오는 경우")
    void create_null() throws Exception {
        //given

        //when then
        assertAll(
                () -> assertThatThrownBy(() -> Post.create(null, "description", List.of(), "shareUrl"))
                        .isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage()),
                () -> assertThatThrownBy(() -> Post.create(1L, null, List.of(), "shareUrl"))
                        .isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage()),
                () -> assertThatThrownBy(() -> Post.create(1L, "description", List.of(), null))
                        .isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage()),
                () -> assertThatThrownBy(() -> Post.create(1L, "description", null, "shareUrl"))
                        .isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage())
        );
    }
}
