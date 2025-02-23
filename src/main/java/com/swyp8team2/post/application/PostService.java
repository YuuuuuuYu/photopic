package com.swyp8team2.post.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.post.presentation.dto.PostResponse;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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

    public PostResponse find(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<PostImage> images = post.getImages();
        int totalVoteCount = 0;
        for (PostImage image : images) {
            totalVoteCount += image.getVoteCount();
        }
        BigDecimal totalCount = new BigDecimal(totalVoteCount);
        for (PostImage image : images) {
            BigDecimal voteCount = new BigDecimal(image.getVoteCount());
            String voteRatio = voteCount.divide(totalCount, 2, RoundingMode.HALF_UP).toString();
        }
        return null;
    }
}
