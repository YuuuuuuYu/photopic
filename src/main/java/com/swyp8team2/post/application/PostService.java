package com.swyp8team2.post.application;

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
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RatioCalculator ratioCalculator;
    private final ImageFileRepository imageFileRepository;
    private final VoteRepository voteRepository;
    private final CryptoService shareUrlCryptoService;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            RatioCalculator ratioCalculator,
            ImageFileRepository imageFileRepository,
            VoteRepository voteRepository,
            @ShareUrlCryptoService CryptoService shareUrlCryptoService
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.ratioCalculator = ratioCalculator;
        this.imageFileRepository = imageFileRepository;
        this.voteRepository = voteRepository;
        this.shareUrlCryptoService = shareUrlCryptoService;
    }

    @Transactional
    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        List<PostImage> postImages = createPostImages(request);
        Post post = Post.create(userId, request.description(), postImages);
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
        Post post = postRepository.findByIdFetchPostImage(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        User author = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<PostImageResponse> votes = createPostImageResponse(userId, postId, post);
        boolean isAuthor = post.getUserId().equals(userId);
        return PostResponse.of(post, author, votes, isAuthor);
    }

    private List<PostImageResponse> createPostImageResponse(Long userId, Long postId, Post post) {
        List<PostImage> images = post.getImages();
        return images.stream()
                .map(image -> createVoteResponseDto(image, userId, postId))
                .toList();
    }

    private PostImageResponse createVoteResponseDto(PostImage image, Long userId, Long postId) {
        ImageFile imageFile = imageFileRepository.findById(image.getImageFileId())
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        boolean voted = Objects.nonNull(userId) && getVoted(image, userId, postId);
        return new PostImageResponse(
                image.getId(),
                image.getName(),
                imageFile.getImageUrl(),
                imageFile.getThumbnailUrl(),
                voted
        );
    }

    private Boolean getVoted(PostImage image, Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return voteRepository.findByUserIdAndPostId(user.getId(), postId)
                .map(vote -> vote.getPostImageId().equals(image.getId()))
                .orElse(false);
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findUserPosts(Long userId, Long cursor, int size) {
        Slice<Post> postSlice = postRepository.findByUserId(userId, cursor, PageRequest.ofSize(size));
        return getCursorPaginatedResponse(postSlice);
    }

    private SimplePostResponse getSimplePostResponse(Post post, List<ImageFile> imageIds) {
        ImageFile bestPickedImage = imageIds.stream()
                .filter(imageFile -> imageFile.getId().equals(post.getBestPickedImage().getImageFileId()))
                .findFirst()
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return SimplePostResponse.of(post, bestPickedImage.getThumbnailUrl());
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

    public List<PostImageVoteStatusResponse> findPostStatus(Long postId) {
        Post post = postRepository.findByIdFetchPostImage(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        int totalVoteCount = getTotalVoteCount(post.getImages());
        return post.getImages().stream()
                .map(image -> {
                    String ratio = ratioCalculator.calculate(totalVoteCount, image.getVoteCount());
                    return new PostImageVoteStatusResponse(image.getId(), image.getName(), image.getVoteCount(), ratio);
                }).toList();
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
        post.validateOwner(userId);
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
}
