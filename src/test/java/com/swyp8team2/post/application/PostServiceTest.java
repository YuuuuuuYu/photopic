package com.swyp8team2.post.application;

import com.swyp8team2.common.annotation.ShareUrlCryptoService;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.domain.Status;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.CreatePostResponse;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.PostImageRequestDto;
import com.swyp8team2.post.presentation.dto.PostImageResponse;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.application.VoteService;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.ArrayList;
import java.util.List;

import static com.swyp8team2.support.fixture.FixtureGenerator.createImageFile;
import static com.swyp8team2.support.fixture.FixtureGenerator.createPost;
import static com.swyp8team2.support.fixture.FixtureGenerator.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class PostServiceTest extends IntegrationTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageFileRepository imageFileRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    VoteService voteService;

    @MockitoBean
    @ShareUrlCryptoService
    CryptoService shareUrlCryptoService;

    @Test
    @DisplayName("게시글 작성")
    void create() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest("description", List.of(
                new PostImageRequestDto(1L),
                new PostImageRequestDto(2L)
        ));
        String shareUrl = "shareUrl";
        given(shareUrlCryptoService.encrypt(any()))
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
                () -> assertThat(images).hasSize(2),
                () -> assertThat(images.get(0).getImageFileId()).isEqualTo(1L),
                () -> assertThat(images.get(0).getName()).isEqualTo("뽀또 A"),
                () -> assertThat(images.get(0).getVoteCount()).isEqualTo(0),
                () -> assertThat(images.get(1).getImageFileId()).isEqualTo(2L),
                () -> assertThat(images.get(1).getName()).isEqualTo("뽀또 B"),
                () -> assertThat(images.get(1).getVoteCount()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("게시글 작성 - 이미지가 2개 미만인 경우")
    void create_invalidPostImageCount() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest("description", List.of(
                new PostImageRequestDto(1L)
        ));

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
        CreatePostRequest request = new CreatePostRequest("a".repeat(101), List.of(
                new PostImageRequestDto(1L),
                new PostImageRequestDto(2L)
        ));

        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("게시글 조회")
    void findById() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));

        //when
        PostResponse response = postService.findById(user.getId(), post.getId());

        //then
        List<PostImageResponse> votes = response.images();
        assertAll(
                () -> assertThat(response.description()).isEqualTo(post.getDescription()),
                () -> assertThat(response.id()).isEqualTo(post.getId()),
                () -> assertThat(response.author().nickname()).isEqualTo(user.getNickname()),
                () -> assertThat(response.author().profileUrl()).isEqualTo(user.getProfileUrl()),
                () -> assertThat(response.shareUrl()).isEqualTo(post.getShareUrl()),
                () -> assertThat(votes).hasSize(2),
                () -> assertThat(votes.get(0).imageUrl()).isEqualTo(imageFile1.getImageUrl()),
                () -> assertThat(votes.get(0).voted()).isFalse(),
                () -> assertThat(votes.get(1).imageUrl()).isEqualTo(imageFile2.getImageUrl()),
                () -> assertThat(votes.get(1).voted()).isFalse()
        );
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 - 커서 null인 경우")
    void findMyPosts() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user);
        int size = 10;

        //when
        var response = postService.findMyPosts(user.getId(), null, size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(posts.size() - size).getId())
        );
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 - 커서 있는 경우")
    void findMyPosts2() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user);
        int size = 10;

        //when
        var response = postService.findMyPosts(user.getId(), posts.get(3).getId(), size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(3),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(0).getId())
        );
    }

    private List<Post> createPosts(User user) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = imageFileRepository.save(createImageFile(i));
            ImageFile imageFile2 = imageFileRepository.save(createImageFile(i + 1));
            posts.add(postRepository.save(createPost(user.getId(), imageFile1, imageFile2, i)));
        }
        return posts;
    }

    @Test
    @DisplayName("내가 투표한 게시글 조회 - 커서 null인 경우")
    void findVotedPosts() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user);
        for (int i = 0; i < 15; i++) {
            Post post = posts.get(i);
            voteRepository.save(Vote.of(post.getId(), post.getImages().get(0).getId(), user.getId()));
        }
        int size = 10;

        //when
        var response = postService.findVotedPosts(user.getId(), null, size);

        //then
        int 전체_15개에서_맨_마지막_데이터_인덱스 = posts.size() - size;
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(전체_15개에서_맨_마지막_데이터_인덱스).getId())
        );
    }

    @Test
    @DisplayName("투표 현황 조회")
    void findPostStatus() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));
        voteService.vote(user.getId(), post.getId(), post.getImages().get(0).getId());

        //when
        var response = postService.findPostStatus(post.getId());

        //then
        assertAll(
                () -> assertThat(response).hasSize(2),
                () -> assertThat(response.get(0).id()).isEqualTo(post.getImages().get(0).getId()),
                () -> assertThat(response.get(0).imageName()).isEqualTo(post.getImages().get(0).getName()),
                () -> assertThat(response.get(0).voteCount()).isEqualTo(1),
                () -> assertThat(response.get(0).voteRatio()).isEqualTo("100.0"),
                () -> assertThat(response.get(1).id()).isEqualTo(post.getImages().get(1).getId()),
                () -> assertThat(response.get(1).imageName()).isEqualTo(post.getImages().get(1).getName()),
                () -> assertThat(response.get(1).voteCount()).isEqualTo(0),
                () -> assertThat(response.get(1).voteRatio()).isEqualTo("0.0")
        );
    }

    @Test
    @DisplayName("투표 마감")
    void close() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));

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
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));

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
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));
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
        Post post = postRepository.save(createPost(user.getId(), imageFile1, imageFile2, 1));

        //when
        postService.delete(user.getId(), post.getId());

        //then
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }
}
