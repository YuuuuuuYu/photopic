package com.swyp8team2.post.domain;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
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

        //when
        Post post = Post.create(userId, description, postImages);

        //then
        List<PostImage> images = post.getImages();
        assertAll(
                () -> assertThat(post.getUserId()).isEqualTo(userId),
                () -> assertThat(post.getDescription()).isEqualTo(description),
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
        assertThatThrownBy(() -> Post.create(1L, "description", postImages))
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
        assertThatThrownBy(() -> Post.create(1L, description, postImages))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("투표 마감")
    void close() throws Exception {
        //given
        long userId = 1L;
        List<PostImage> postImages = List.of(
                PostImage.create("뽀또A", 1L),
                PostImage.create("뽀또B", 2L)
        );
        Post post = new Post(null, userId, "description", State.PROGRESS, postImages, "shareUrl");

        //when
        post.close(userId);

        //then
        assertThat(post.getState()).isEqualTo(State.CLOSED);
    }

    @Test
    @DisplayName("투표 마감 - 이미 마감된 게시글인 경우")
    void close_alreadyClosed() throws Exception {
        //given
        long userId = 1L;
        List<PostImage> postImages = List.of(
                PostImage.create("뽀또A", 1L),
                PostImage.create("뽀또B", 2L)
        );
        Post post = new Post(null, userId, "description", State.CLOSED, postImages, "shareUrl");

        //when then
        assertThatThrownBy(() -> post.close(userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표 마감 - 게시글 작성자가 아닌 경우")
    void close_notPostAuthor() throws Exception {
        //given
        long userId = 1L;
        List<PostImage> postImages = List.of(
                PostImage.create("뽀또A", 1L),
                PostImage.create("뽀또B", 2L)
        );
        Post post = new Post(null, userId, "description", State.PROGRESS, postImages, "shareUrl");

        //when then
        assertThatThrownBy(() -> post.close(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_POST_AUTHOR.getMessage());
    }
}
