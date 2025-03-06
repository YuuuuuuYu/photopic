package com.swyp8team2.post.infrastructure;

import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostRepository;
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

class PostQueryRepositoryTest extends RepositoryTest {

    @Autowired
    PostQueryRepository postQueryRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("유저가 작성한 게시글 조회 - 게시글이 15개일 경우 15번쨰부터 10개 조회해야 함")
    void select_post_findByUserId1() throws Exception {
        //given
        long userId = 1L;
        List<Post> posts = createPosts(userId);
        int size = 10;

        //when
        Slice<Post> res = postQueryRepository.findByUserId(userId, null, PageRequest.ofSize(size));

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
        List<Post> posts = createPosts(userId);
        int size = 10;
        int cursorIndex = 5;

        //when
        Slice<Post> res = postQueryRepository.findByUserId(userId, posts.get(cursorIndex).getId(), PageRequest.ofSize(size));

        //then
        assertAll(
                () -> assertThat(res.getContent().size()).isEqualTo(5),
                () -> assertThat(res.getContent().get(0).getId()).isEqualTo(posts.get(cursorIndex - 1).getId()),
                () -> assertThat(res.getContent().get(1).getId()).isEqualTo(posts.get(cursorIndex - 2).getId()),
                () -> assertThat(res.getContent().get(2).getId()).isEqualTo(posts.get(cursorIndex - 3).getId()),
                () -> assertThat(res.hasNext()).isFalse()
        );
    }

    private List<Post> createPosts(long userId) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 30; i += 2) {
            ImageFile imageFile1 = createImageFile(i);
            ImageFile imageFile2 = createImageFile(i + 1);
            posts.add(postRepository.save(createPost(userId, imageFile1, imageFile2, i)));
        }
        return posts;
    }
}
