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
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void createComment(Long postId, CommentRequest request, UserInfo userInfo) {
        Comment comment = new Comment(postId, userInfo.userId(), request.content());
        commentRepository.save(comment);
    }

    public CursorBasePaginatedResponse<CommentResponse> findComments(Long userId, Long postId, Long cursor, int size) {
        Slice<Comment> commentSlice = commentRepository.findByPostId(postId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(
                commentSlice.map(comment -> createCommentResponse(comment, userId))
        );
    }

    private CommentResponse createCommentResponse(Comment comment, Long userId) {
        User author = userRepository.findById(comment.getUserNo())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<Vote> votes = voteRepository.findByUserIdAndPostId(userId, comment.getPostId());
        List<Long> voteImageIds = votes.stream()
                .map(Vote::getPostImageId)
                .collect(Collectors.toList());
        return CommentResponse.of(comment, author, author.getId().equals(userId), voteImageIds);
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest request, UserInfo userInfo) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserNo().equals(userInfo.userId())) {
            throw new ForbiddenException();
        }

        comment.updateComment(request.content());
    }

    @Transactional
    public void deleteComment(Long commentId, UserInfo userInfo) {
        Comment comment = commentRepository.findByIdAndNotDeleted(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUserNo().equals(userInfo.userId())) {
            throw new ForbiddenException();
        }

        comment.delete();
    }
}
