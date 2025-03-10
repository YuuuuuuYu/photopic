package com.swyp8team2.post.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.*;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.CreatePostResponse;
import com.swyp8team2.post.presentation.dto.PostImageRequestDto;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;

import static com.swyp8team2.support.fixture.FixtureGenerator.createImageFile;
import static com.swyp8team2.support.fixture.FixtureGenerator.createPost;
import static com.swyp8team2.support.fixture.FixtureGenerator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class PostCommandServiceTest extends IntegrationTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageFileRepository imageFileRepository;

    @MockitoBean
    ShareUrlService shareUrlShareUrlService;

    @Test
    @DisplayName("게시글 작성")
    void create() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest(
                "description",
                List.of(
                        new PostImageRequestDto(1L),
                        new PostImageRequestDto(2L)
                ),
                Scope.PRIVATE,
                VoteType.SINGLE
        );
        String shareUrl = "shareUrl";
        given(shareUrlShareUrlService.encrypt(any()))
                .willReturn(shareUrl);

        //when
        CreatePostResponse response = postService.create(userId, request);

        //then
        Post post = postRepository.findById(response.postId()).get();
        List<PostImage> images = post.getImages();
        assertAll(
                () -> assertThat(post.getDescription()).isEqualTo("description"),
                () -> assertThat(post.getUserId()).isEqualTo(userId),
                () -> assertThat(post.getShareUrl()).isEqualTo(shareUrl),
                () -> assertThat(post.getStatus()).isEqualTo(Status.PROGRESS),
                () -> assertThat(post.getVoteType()).isEqualTo(VoteType.SINGLE),
                () -> assertThat(images).hasSize(2),
                () -> assertThat(images.get(0).getImageFileId()).isEqualTo(1L),
                () -> assertThat(images.get(0).getName()).isEqualTo("뽀또A"),
                () -> assertThat(images.get(0).getVoteCount()).isEqualTo(0),
                () -> assertThat(images.get(1).getImageFileId()).isEqualTo(2L),
                () -> assertThat(images.get(1).getName()).isEqualTo("뽀또B"),
                () -> assertThat(images.get(1).getVoteCount()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("게시글 작성 - 이미지가 2개 미만인 경우")
    void create_invalidPostImageCount() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest(
                "description",
                List.of(
                        new PostImageRequestDto(1L)
                ),
                Scope.PRIVATE,
                VoteType.SINGLE
        );
        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_POST_IMAGE_COUNT.getMessage());
    }

    @Test
    @DisplayName("게시글 작성 - 설명이 100자 넘어가는 경우")
    void create_descriptionCountExceeded() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest(
                "a".repeat(101),
                List.of(
                        new PostImageRequestDto(1L),
                        new PostImageRequestDto(2L)
                ),
                Scope.PRIVATE,
                VoteType.SINGLE
        );

        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("투표 마감")
    void close() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));

        //when
        post.close(user.getId());

        //then
        postRepository.findById(post.getId()).get();
        assertThat(post.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    @DisplayName("투표 마감 - 게시글 작성자가 아닐 경우")
    void close_notPostAuthor() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));

        //when then
        assertThatThrownBy(() -> post.close(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_POST_AUTHOR.getMessage());
    }

    @Test
    @DisplayName("투표 마감 - 이미 마감된 게시글인 경우")
    void close_alreadyClosed() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
        post.close(user.getId());

        //when then
        assertThatThrownBy(() -> post.close(user.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표 마감 - 존재하지 않는 게시글일 경우")
    void close_notFoundPost() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> postService.close(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));

        //when
        postService.delete(user.getId(), post.getId());

        //then
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    private List<Post> createPosts(User user) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = imageFileRepository.save(createImageFile(i));
            ImageFile imageFile2 = imageFileRepository.save(createImageFile(i + 1));
            posts.add(postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, i)));
        }
        return posts;
    }

}
