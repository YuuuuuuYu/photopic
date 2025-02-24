package com.swyp8team2.comment.application;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.comment.domain.Comment;
import com.swyp8team2.comment.domain.CommentRepository;
import com.swyp8team2.comment.presentation.dto.CommentResponse;
import com.swyp8team2.comment.presentation.dto.CreateCommentRequest;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("댓글 생성")
    void createComment() {
        // given
        Long postId = 1L;
        CreateCommentRequest request = new CreateCommentRequest("테스트 댓글");
        UserInfo userInfo = new UserInfo(100L);
        Comment comment = new Comment(postId, userInfo.userId(), request.content());

        // when
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // then
        assertDoesNotThrow(() -> commentService.createComment(postId, request, userInfo));
    }

    @Test
    @DisplayName("댓글 조회")
    void selectComments() {
        // given
        Long postId = 1L;
        Long cursor = null;
        int size = 2;

        Comment comment1 = new Comment(1L, postId, 100L, "첫 번째 댓글");
        Comment comment2 = new Comment(2L, postId, 100L, "두 번째 댓글");
        SliceImpl<Comment> commentSlice = new SliceImpl<>(List.of(comment1, comment2), PageRequest.of(0, size), false);
        User user = new User(100L, "닉네임","http://example.com/profile.png");

        // Mock 설정
        given(commentRepository.findByPostId(eq(postId), eq(cursor), any(PageRequest.class))).willReturn(commentSlice);
        // 각 댓글마다 user_no=100L 이므로, findById(100L)만 호출됨
        given(userRepository.findById(100L)).willReturn(Optional.of(user));

        // when
        CursorBasePaginatedResponse<CommentResponse> response = commentService.selectComments(postId, cursor, size);

        // then
        assertThat(response.data()).hasSize(2);

        CommentResponse cr1 = response.data().get(0);
        assertThat(cr1.commentId()).isEqualTo(1L);
        assertThat(cr1.content()).isEqualTo("첫 번째 댓글");
        assertThat(cr1.author().nickname()).isEqualTo("닉네임");

        CommentResponse cr2 = response.data().get(1);
        assertThat(cr2.commentId()).isEqualTo(2L);
        assertThat(cr2.content()).isEqualTo("두 번째 댓글");
    }

}
