package com.swyp8team2.post.domain;

import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.support.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

import static com.swyp8team2.support.fixture.FixtureGenerator.createImageFile;
import static com.swyp8team2.support.fixture.FixtureGenerator.createPost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostRepositoryTest extends RepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 게시글이 15개일 경우 15번쨰부터 10개 조회해야 함")
    void select_post_findByUserId1() throws Exception {
        //given
        long userId = 1L;
        List<Post> posts = createPosts(userId, Scope.PRIVATE);
        int size = 10;

        //when
        Slice<Post> res = postRepository.findByUserId(userId, null, PageRequest.ofSize(size));

        //then
        assertAll(
                () -> assertThat(res.getContent().size()).isEqualTo(size),
                () -> assertThat(res.getContent().get(0).getId()).isEqualTo(posts.get(posts.size() - 1).getId()),
                () -> assertThat(res.getContent().get(1).getId()).isEqualTo(posts.get(posts.size() - 2).getId()),
                () -> assertThat(res.getContent().get(2).getId()).isEqualTo(posts.get(posts.size() - 3).getId()),
                () -> assertThat(res.hasNext()).isTrue()
        );
    }

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 15개 중에 커서가 5번째 게시글의 id면 4번째부터 0번째까지 조회해야 함")
    void select_post_findByUserId2() throws Exception {
        //given
        long userId = 1L;
        List<Post> posts = createPosts(userId, Scope.PRIVATE);
        int size = 10;
        int cursorIndex = 5;

        //when
        Slice<Post> res = postRepository.findByUserId(userId, posts.get(cursorIndex).getId(), PageRequest.ofSize(size));

        //then
        assertAll(
                () -> assertThat(res.getContent().size()).isEqualTo(5),
                () -> assertThat(res.getContent().get(0).getId()).isEqualTo(posts.get(cursorIndex - 1).getId()),
                () -> assertThat(res.getContent().get(1).getId()).isEqualTo(posts.get(cursorIndex - 2).getId()),
                () -> assertThat(res.getContent().get(2).getId()).isEqualTo(posts.get(cursorIndex - 3).getId()),
                () -> assertThat(res.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("id 리스트에 포함되는 게시글 조회")
    void select_post_findByIdIn() throws Exception {
        //given
        List<Post> posts = createPosts(1L, Scope.PRIVATE);
        List<Long> postIds = List.of(posts.get(0).getId(), posts.get(1).getId(), posts.get(2).getId());

        //when
        Slice<Post> postSlice = postRepository.findByIdIn(postIds, null, PageRequest.ofSize(10));

        //then
        assertAll(
                () -> assertThat(postSlice.getContent().size()).isEqualTo(postIds.size()),
                () -> assertThat(postSlice.getContent().get(0).getId()).isEqualTo(postIds.get(2)),
                () -> assertThat(postSlice.getContent().get(1).getId()).isEqualTo(postIds.get(1)),
                () -> assertThat(postSlice.getContent().get(2).getId()).isEqualTo(postIds.get(0)),
                () -> assertThat(postSlice.hasNext()).isFalse()
        );
    }

    private List<Post> createPosts(long userId, Scope scope) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = createImageFile(i);
            ImageFile imageFile2 = createImageFile(i + 1);
            posts.add(postRepository.save(createPost(userId, scope, imageFile1, imageFile2, i)));
        }
        return posts;
    }

    @Test
    @DisplayName("피드 조회")
    void select_post_findByScopeAndDeletedFalse() {
        //given
        List<Post> myPosts = createPosts(1L, Scope.PRIVATE);
        List<Post> privatePosts = createPosts(2L, Scope.PRIVATE);
        List<Post> publicPosts = createPosts(2L, Scope.PUBLIC);
        int size = 10;

        //when
        Slice<Post> res = postRepository.findByScopeAndDeletedFalse(1L, null, PageRequest.ofSize(size));

        //then
        assertAll(
                () -> assertThat(res.getContent().size()).isEqualTo(size),
                () -> assertThat(res.hasNext()).isTrue()
        );
    }
}
