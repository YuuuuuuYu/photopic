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
import com.swyp8team2.post.presentation.dto.SimplePostResponse;
import com.swyp8team2.post.presentation.dto.VoteResponseDto;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RatioCalculator ratioCalculator;
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public Long create(Long userId, CreatePostRequest request) {
        List<PostImage> postImages = createPostImages(request);
        Post post = Post.create(userId, request.description(), postImages, "TODO: location");
        Post save = postRepository.save(post);
        return save.getId();
    }

    private List<PostImage> createPostImages(CreatePostRequest request) {
        PostImageNameGenerator nameGenerator = new PostImageNameGenerator();
        return request.votes().stream()
                .map(voteRequestDto -> PostImage.create(
                        nameGenerator.generate(),
                        voteRequestDto.imageFileId()
                )).toList();
    }

    public PostResponse findById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<PostImage> images = post.getImages();
        List<VoteResponseDto> votes = images.stream()
                .map(image -> createVoteResponseDto(image, images))
                .toList();
        return PostResponse.of(post, user, votes);
    }

    private VoteResponseDto createVoteResponseDto(PostImage image, List<PostImage> images) {
        ImageFile imageFile = imageFileRepository.findById(image.getImageFileId())
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return new VoteResponseDto(
                image.getId(),
                imageFile.getImageUrl(),
                image.getVoteCount(),
                ratioCalculator.calculateRatio(getTotalVoteCount(images), image.getVoteCount()),
                false //TODO: implement
        );
    }

    private int getTotalVoteCount(List<PostImage> images) {
        int totalVoteCount = 0;
        for (PostImage image : images) {
            totalVoteCount += image.getVoteCount();
        }
        return totalVoteCount;
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findMyPosts(Long userId, Long cursor, int size) {
        Slice<Post> postSlice = postRepository.findByUserId(userId, cursor, PageRequest.of(0, size));
        return CursorBasePaginatedResponse.of(postSlice.map(this::createSimplePostResponse)
        );
    }

    private SimplePostResponse createSimplePostResponse(Post post) {
        ImageFile bestPickedImage = imageFileRepository.findById(post.getBestPickedImage().getImageFileId())
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return SimplePostResponse.of(post, bestPickedImage.getThumbnailUrl());
    }
}
