package com.swyp8team2.post.application;

import com.swyp8team2.comment.domain.Comment;
import com.swyp8team2.comment.domain.CommentRepository;
import com.swyp8team2.common.annotation.ShareUrlCryptoService;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.CreatePostResponse;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.PostImageVoteStatusResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.PostImageResponse;
import com.swyp8team2.post.presentation.dto.FeedResponse;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RatioCalculator ratioCalculator;
    private final ImageFileRepository imageFileRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final CryptoService shareUrlCryptoService;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            RatioCalculator ratioCalculator,
            ImageFileRepository imageFileRepository,
            VoteRepository voteRepository,
            CommentRepository commentRepository,
            @ShareUrlCryptoService CryptoService shareUrlCryptoService
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.ratioCalculator = ratioCalculator;
        this.imageFileRepository = imageFileRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
        this.shareUrlCryptoService = shareUrlCryptoService;
    }

    @Transactional
    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        List<PostImage> postImages = createPostImages(request);
        Post post = Post.create(userId, request.description(), postImages, request.scope(), request.voteType());
        Post save = postRepository.save(post);
        save.setShareUrl(shareUrlCryptoService.encrypt(String.valueOf(save.getId())));
        return new CreatePostResponse(save.getId(), save.getShareUrl());
    }

    private List<PostImage> createPostImages(CreatePostRequest request) {
        PostImageNameGenerator nameGenerator = new PostImageNameGenerator();
        return request.images().stream()
                .map(voteRequestDto -> PostImage.create(
                        nameGenerator.generate(),
                        voteRequestDto.imageFileId()
                )).toList();
    }

    public PostResponse findById(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
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
        return CursorBasePaginatedResponse.of(postSlice.map(this::createSimplePostResponse)
        );
    }

    private SimplePostResponse createSimplePostResponse(Post post) {
        ImageFile bestPickedImage = imageFileRepository.findById(post.getBestPickedImage().getImageFileId())
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return SimplePostResponse.of(post, bestPickedImage.getThumbnailUrl());
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findVotedPosts(Long userId, Long cursor, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<Long> postIds = voteRepository.findByUserId(user.getId())
                .map(Vote::getPostId)
                .toList();
        Slice<Post> postSlice = postRepository.findByIdIn(postIds, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(this::createSimplePostResponse));
    }

    public List<PostImageVoteStatusResponse> findVoteStatus(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        validateVoteStatus(userId, post);
        int totalVoteCount = getTotalVoteCount(post.getImages());
        return post.getImages().stream()
                .map(image -> {
                    String ratio = ratioCalculator.calculate(totalVoteCount, image.getVoteCount());
                    return new PostImageVoteStatusResponse(image.getId(), image.getName(), image.getVoteCount(), ratio);
                }).toList();
    }

    private void validateVoteStatus(Long userId, Post post) {
        boolean voted = voteRepository.findByUserIdAndPostId(userId, post.getId())
                .isPresent();
        if (!(post.isAuthor(userId) || voted)) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED_VOTE_STATUS);
        }
    }

    private int getTotalVoteCount(List<PostImage> images) {
        int totalVoteCount = 0;
        for (PostImage image : images) {
            totalVoteCount += image.getVoteCount();
        }
        return totalVoteCount;
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        if (!post.isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        postRepository.delete(post);
    }

    @Transactional
    public void close(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.close(userId);
    }

    public PostResponse findByShareUrl(Long userId, String shareUrl) {
        String decrypt = shareUrlCryptoService.decrypt(shareUrl);
        return findById(userId, Long.valueOf(decrypt));
    }

    @Transactional
    public void toggleScope(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.toggleScope(userId);
    }

    public CursorBasePaginatedResponse<FeedResponse> findFeed(Long userId, Long cursor, int size) {
        Slice<Post> postSlice = postRepository.findByScopeAndDeletedFalse(userId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(post -> createFeedResponse(userId, post)));
    }

    private FeedResponse createFeedResponse(Long userId, Post post) {
        List<PostImageResponse> images = createPostImageResponse(userId, post);
        List<Vote> votes = voteRepository.findByPostIdAndDeletedFalse(post.getId());
        List<Comment> comments = commentRepository.findByPostIdAndDeletedFalse(post.getId());
        boolean isAuthor = post.getUserId().equals(userId);

        return FeedResponse.of(post, images, votes.size(), comments.size(), isAuthor);
    }
}
