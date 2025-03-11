package com.swyp8team2.post.application;

import com.swyp8team2.comment.domain.Comment;
import com.swyp8team2.comment.domain.CommentRepository;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.presentation.dto.FeedResponse;
import com.swyp8team2.post.presentation.dto.PostImageResponse;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageFileRepository imageFileRepository;
    private final VoteRepository voteRepository;
    private final ShareUrlService shareUrlShareUrlService;
    private final CommentRepository commentRepository;

    public PostResponse findByShareUrl(Long userId, String shareUrl) {
        String decrypt = shareUrlShareUrlService.decrypt(shareUrl);
        return findById(userId, Long.valueOf(decrypt));
    }

    public PostResponse findById(Long userId, Long postId) {
        Post post = postRepository.findByIdFetchPostImage(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        User author = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<PostImageResponse> votes = createPostImageResponse(userId, post);
        boolean isAuthor = post.getUserId().equals(userId);
        return PostResponse.of(post, author, votes, isAuthor);
    }

    private List<PostImageResponse> createPostImageResponse(Long userId, Post post) {
        List<PostImage> images = post.getImages();
        return images.stream()
                .map(image -> createVoteResponseDto(image, userId))
                .toList();
    }

    private PostImageResponse createVoteResponseDto(PostImage image, Long userId) {
        ImageFile imageFile = imageFileRepository.findById(image.getImageFileId())
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return new PostImageResponse(
                image.getId(),
                image.getName(),
                imageFile.getImageUrl(),
                imageFile.getThumbnailUrl(),
                getVoteId(image, userId)
        );
    }

    private Long getVoteId(PostImage image, Long userId) {
        return voteRepository.findByUserIdAndPostImageId(userId, image.getId())
                .map(Vote::getId)
                .orElse(null);
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findUserPosts(Long userId, Long cursor, int size) {
        Slice<Post> postSlice = postRepository.findByUserId(userId, cursor, PageRequest.ofSize(size));
        return getCursorPaginatedResponse(postSlice);
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findVotedPosts(Long userId, Long cursor, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<Long> votedPostIds = voteRepository.findByUserId(user.getId())
                .map(Vote::getPostId)
                .toList();
        Slice<Post> votedPostSlice = postRepository.findByIdIn(votedPostIds, cursor, PageRequest.ofSize(size));

        return getCursorPaginatedResponse(votedPostSlice);
    }

    private CursorBasePaginatedResponse<SimplePostResponse> getCursorPaginatedResponse(Slice<Post> postSlice) {
        List<Long> bestPickedImageIds = postSlice.getContent().stream()
                .map(Post::getBestPickedImage)
                .map(PostImage::getImageFileId)
                .toList();
        List<ImageFile> imageIds = imageFileRepository.findByIdIn(bestPickedImageIds);

        List<SimplePostResponse> responseContent = postSlice.getContent().stream()
                .map(post -> getSimplePostResponse(post, imageIds))
                .toList();

        return CursorBasePaginatedResponse.of(new SliceImpl<>(
                responseContent,
                postSlice.getPageable(),
                postSlice.hasNext()
        ));
    }

    private SimplePostResponse getSimplePostResponse(Post post, List<ImageFile> imageIds) {
        ImageFile bestPickedImage = imageIds.stream()
                .filter(imageFile -> imageFile.getId().equals(post.getBestPickedImage().getImageFileId()))
                .findFirst()
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return SimplePostResponse.of(post, bestPickedImage.getThumbnailUrl());
    }

    public CursorBasePaginatedResponse<FeedResponse> findFeed(Long userId, Long cursor, int size) {
        Slice<Post> postSlice = postRepository.findByScopeAndDeletedFalse(userId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(post -> createFeedResponse(userId, post)));
    }

    private FeedResponse createFeedResponse(Long userId, Post post) {
        User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<PostImageResponse> images = createPostImageResponse(userId, post);
        List<Vote> votes = voteRepository.findByPostIdAndDeletedFalse(post.getId());
        List<Comment> comments = commentRepository.findByPostIdAndDeletedFalse(post.getId());
        boolean isAuthor = post.getUserId().equals(userId);

        return FeedResponse.of(post, user, images, votes.size(), comments.size(), isAuthor);
    }
}
