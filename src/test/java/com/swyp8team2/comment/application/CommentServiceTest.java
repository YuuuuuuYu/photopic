package com.swyp8team2.comment.application;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.comment.domain.Comment;
import com.swyp8team2.comment.domain.CommentRepository;
import com.swyp8team2.comment.presentation.dto.CommentResponse;
import com.swyp8team2.comment.presentation.dto.CommentRequest;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.ForbiddenException;
import com.swyp8team2.user.domain.Role;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private VoteRepository voteRepository;

    @Test
    @DisplayName("댓글 생성")
    void createComment() {
        // given
        Long postId = 1L;
        CommentRequest request = new CommentRequest("테스트 댓글");
        UserInfo userInfo = new UserInfo(100L, Role.USER);
        Comment comment = new Comment(postId, userInfo.userId(), request.content());

        // when
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // then
        assertDoesNotThrow(() -> commentService.createComment(postId, request, userInfo));
    }

    @Test
    @DisplayName("댓글 조회")
    void findComments() {
        // given
        Long postId = 1L;
        Long cursor = null;
        int size = 2;

        Comment comment1 = new Comment(1L, postId, 100L, "첫 번째 댓글");
        Comment comment2 = new Comment(2L, postId, 100L, "두 번째 댓글");
        SliceImpl<Comment> commentSlice = new SliceImpl<>(List.of(comment1, comment2), PageRequest.of(0, size), false);
        User user = new User(100L, "닉네임","http://example.com/profile.png", Role.USER);

        // Mock 설정
        given(commentRepository.findByPostId(eq(postId), eq(cursor), any(PageRequest.class))).willReturn(commentSlice);
        given(voteRepository.findByUserIdAndPostId(eq(user.getId()), eq(postId))).willReturn(empty());
        // 각 댓글마다 user_no=100L 이므로, findById(100L)만 호출됨
        given(userRepository.findById(100L)).willReturn(Optional.of(user));

        // when
        CursorBasePaginatedResponse<CommentResponse> response = commentService.findComments(user.getId(), postId, cursor, size);

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

    @Test
    @DisplayName("댓글 조회 - 유저 정보 없는 경우")
    void findComments_userNotFound() {
        // given
        Long postId = 1L;
        Long cursor = null;
        int size = 2;

        Comment comment1 = new Comment(1L, postId, 100L, "첫 번째 댓글");
        Comment comment2 = new Comment(2L, postId, 100L, "두 번째 댓글");
        SliceImpl<Comment> commentSlice = new SliceImpl<>(
                List.of(comment1, comment2),
                PageRequest.of(0, size),
                false
        );

        given(commentRepository.findByPostId(eq(postId), eq(cursor), any(PageRequest.class))).willReturn(commentSlice);
        given(userRepository.findById(100L)).willReturn(empty());

        // when & then
        assertThatThrownBy(() -> commentService.findComments(1L, postId, cursor, size))
                .isInstanceOf(BadRequestException.class)
                .hasMessage((ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        // given
        Long postId = 1L;
        UserInfo userInfo = new UserInfo(100L, Role.USER);
        Comment comment = new Comment(1L, postId, 100L, "첫 번째 댓글");
        CommentRequest request = new CommentRequest("수정 댓글");
        when(commentRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(comment));

        // when
        commentService.updateComment(1L, request, userInfo);

        // then
        assertAll(
                () -> assertThat(comment.getId()).isEqualTo(1L),
                () -> assertThat(comment.getContent()).isEqualTo("수정 댓글")
        );
    }

    @Test
    @DisplayName("댓글 수정 - 존재하지 않은 댓글")
    void updateComment_commentNotFound() {
        // given
        CommentRequest request = new CommentRequest("수정 댓글");
        UserInfo userInfo = new UserInfo(100L, Role.USER);
        when(commentRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> commentService.updateComment(1L, request, userInfo))
                .isInstanceOf(BadRequestException.class)
                .hasMessage((ErrorCode.COMMENT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("댓글 수정 - 권한 없는 사용자")
    void updateComment_forbiddenException() {
        // given
        Long postId = 1L;
        UserInfo userInfo = new UserInfo(100L, Role.USER);
        Comment comment = new Comment(1L, postId, 110L, "첫 번째 댓글");
        CommentRequest request = new CommentRequest("수정 댓글");
        when(commentRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(comment));

        // when then
        assertAll(
                () -> assertThatThrownBy(() -> commentService.updateComment(1L, request, userInfo))
                        .isInstanceOf(ForbiddenException.class),
                () -> assertThat(comment.getContent()).isEqualTo("첫 번째 댓글")
        );
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        // given
        Long postId = 1L;
        UserInfo userInfo = new UserInfo(100L, Role.USER);
        Comment comment = new Comment(1L, postId, 100L, "첫 번째 댓글");
        when(commentRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(1L, userInfo);

        // then
        assertAll(
                () -> assertTrue(comment.isDeleted()),
                () -> assertNotNull(comment.getDeletedAt())
        );
    }

    @Test
    @DisplayName("댓글 삭제 - 존재하지 않는 댓글")
    void deleteComment_commentNotFound() {
        // given
        UserInfo userInfo = new UserInfo(100L, Role.USER);
        when(commentRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(1L, userInfo))
                .isInstanceOf(BadRequestException.class)
                .hasMessage((ErrorCode.COMMENT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("댓글 삭제 - 권한 없는 사용자")
    void deleteComment_forbiddenException() {
        // given
        Long postId = 1L;
        UserInfo userInfo = new UserInfo(100L, Role.USER);
        Comment comment = new Comment(1L, postId, 110L, "첫 번째 댓글");
        when(commentRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.of(comment));

        // when then
        assertThatThrownBy(() -> commentService.deleteComment(1L, userInfo))
                .isInstanceOf(ForbiddenException.class);
        assertFalse(comment.isDeleted());
        assertNull(comment.getDeletedAt());
    }
}
