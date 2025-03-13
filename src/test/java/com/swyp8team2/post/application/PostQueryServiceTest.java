package com.swyp8team2.post.application;

import com.swyp8team2.comment.domain.Comment;
import com.swyp8team2.comment.domain.CommentRepository;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.domain.Scope;
import com.swyp8team2.post.presentation.dto.FeedResponse;
import com.swyp8team2.post.presentation.dto.PostImageResponse;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.support.IntegrationTest;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.swyp8team2.support.fixture.FixtureGenerator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PostQueryServiceTest extends IntegrationTest {

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
    CommentRepository commentRepository;

    @Test
    @DisplayName("게시글 조회")
    void findById() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));

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
                () -> assertThat(votes.get(0).voteId()).isNull(),
                () -> assertThat(votes.get(1).imageUrl()).isEqualTo(imageFile2.getImageUrl()),
                () -> assertThat(votes.get(1).voteId()).isNull()
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 커서 null인 경우")
    void findUserPosts() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user, Scope.PRIVATE);
        int size = 10;

        //when
        var response = postService.findUserPosts(user.getId(), null, size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(posts.size() - size).getId())
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 커서 있는 경우")
    void findUserPosts2() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user, Scope.PRIVATE);
        int size = 10;

        //when
        var response = postService.findUserPosts(user.getId(), posts.get(3).getId(), size);

        //then
        assertAll(
                () -> assertThat(response.data()).hasSize(3),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursor()).isEqualTo(posts.get(0).getId())
        );
    }

    private List<Post> createPosts(User user, Scope scope) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = imageFileRepository.save(createImageFile(i));
            ImageFile imageFile2 = imageFileRepository.save(createImageFile(i + 1));
            posts.add(postRepository.save(createPost(user.getId(), scope, imageFile1, imageFile2, i)));
        }
        return posts;
    }

    @Test
    @DisplayName("유저가 투표한 게시글 조회 - 커서 null인 경우")
    void findVotedPosts() throws Exception {
        //given
        User user = userRepository.save(createUser(1));
        List<Post> posts = createPosts(user, Scope.PRIVATE);
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
    @DisplayName("피드 조회 - 내 게시글 1개, 공개 게시글 15개, 투표 10개, 댓글 20개")
    void findFeed() throws Exception {
        //given
        int size = 20;
        User user1 = userRepository.save(createUser(1));
        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));

        Post myPost = postRepository.save(createPost(user1.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
        List<Post> privatePosts = createPosts(userRepository.save(createUser(2)), Scope.PRIVATE);
        List<Post> publicPosts = createPosts(userRepository.save(createUser(2)), Scope.PUBLIC);

        createVotes(user1, myPost);
        createComments(user1, myPost);

        //when
        List<Vote> votes = voteRepository.findByPostIdAndDeletedFalse(myPost.getId());
        List<Comment> comments = commentRepository.findByPostIdAndDeletedFalse(myPost.getId());
        CursorBasePaginatedResponse<FeedResponse> response = postService.findFeed(user1.getId(), null, size);

        //then
        assertAll(
                () -> assertThat(response.data().size()).isEqualTo(16),
                () -> assertThat(response.data().getLast().participantCount()).isEqualTo(votes.size()),
                () -> assertThat(response.data().getLast().commentCount()).isEqualTo(comments.size()),
                () -> assertThat(response.data().getLast().isAuthor()).isTrue(),
                () -> assertThat(response.data().getFirst().isAuthor()).isFalse()
        );
    }

    private void createVotes(User user, Post post) {
        for (int i = 0; i < 5; i++) {
            ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
            ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
            voteRepository.save(createVote(user.getId(), post.getId(), imageFile1.getId()));
            voteRepository.save(createVote(user.getId(), post.getId(), imageFile2.getId()));
        }
    }

    private void createComments(User user, Post post) {
        for (int i = 0; i < 20; i++) {
            commentRepository.save(createComment(user.getId(), post.getId()));
        }
    }
}
