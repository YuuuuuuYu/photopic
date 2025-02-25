package com.swyp8team2.post.application;

import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.post.presentation.dto.PostImageVoteStatusResponse;
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.PostImageResponse;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RatioCalculator ratioCalculator;
    private final ImageFileRepository imageFileRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public Long create(Long userId, CreatePostRequest request) {
        List<PostImage> postImages = createPostImages(request);
        Post post = Post.create(userId, request.description(), postImages, "TODO: location");
        Post save = postRepository.save(post);
        return save.getId();
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
        List<PostImageResponse> votes = createPostImageResponse(userId, postId, post);
        return PostResponse.of(post, author, votes);
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
                voted
        );
    }

    private Boolean getVoted(PostImage image, Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return voteRepository.findByUserSeqAndPostId(user.getSeq(), postId)
                .map(vote -> vote.getPostImageId().equals(image.getId()))
                .orElse(false);
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findMyPosts(Long userId, Long cursor, int size) {
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
        List<Long> postIds = voteRepository.findByUserSeq(user.getSeq())
                .map(Vote::getPostId)
                .toList();
        Slice<Post> postSlice = postRepository.findByIdIn(postIds, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(this::createSimplePostResponse));
    }

    public List<PostImageVoteStatusResponse> findPostStatus(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        int totalVoteCount = getTotalVoteCount(post.getImages());
        return post.getImages().stream()
                .map(image -> {
                    String ratio = ratioCalculator.calculate(image.getVoteCount(), totalVoteCount);
                    return new PostImageVoteStatusResponse(image.getName(), image.getVoteCount(), ratio);
                }).toList();
    }

    private int getTotalVoteCount(List<PostImage> images) {
        int totalVoteCount = 0;
        for (PostImage image : images) {
            totalVoteCount += image.getVoteCount();
        }
        return totalVoteCount;
    }
}
