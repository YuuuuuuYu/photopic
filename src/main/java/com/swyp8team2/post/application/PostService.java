package com.swyp8team2.post.application;

import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.presentation.dto.CreatePostRequest;
import com.swyp8team2.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
