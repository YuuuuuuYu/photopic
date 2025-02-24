package com.swyp8team2.comment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 조회")
    void select_CommentUser() {
        // given
        Comment comment1 = new Comment(1L, 100L, "content1");
        Comment comment2 = new Comment(1L, 101L, "content2");
        Comment comment3 = new Comment(2L, 102L, "content3");
        Comment comment4 = new Comment(1L, 103L, "content4");
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));

        // when
        Slice<Comment> result1 = commentRepository.findByPostId(1L, null, PageRequest.of(0, 10));

        // then
        assertThat(result1.getContent()).hasSize(3);

        // when2
        Slice<Comment> result2 = commentRepository.findByPostId(1L, 1L, PageRequest.of(0, 10));

        // then2
        assertThat(result2.getContent()).hasSize(2);
        assertThat(result2.getContent().getFirst().getUserNo()).isEqualTo(101L);
    }
}