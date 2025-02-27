package com.swyp8team2.common.dev;

import com.swyp8team2.auth.application.jwt.JwtService;
import com.swyp8team2.auth.application.jwt.TokenPair;
import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.domain.ImageFileRepository;
import com.swyp8team2.image.presentation.dto.ImageFileDto;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.user.domain.NicknameAdjective;
import com.swyp8team2.user.domain.NicknameAdjectiveRepository;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.application.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile({"dev", "local"})
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;
    private final UserRepository userRepository;
    private final ImageFileRepository imageFileRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;
    private final VoteService voteService;

    @Transactional
    public void init() {
        List<NicknameAdjective> adjectives = nicknameAdjectiveRepository.findAll();
        User testUser = userRepository.save(User.create("nickname", "https://t1.kakaocdn.net/account_images/default_profile.jpeg"));
        TokenPair tokenPair = jwtService.createToken(testUser.getId());
        System.out.println("accessToken = " + tokenPair.accessToken());
        System.out.println("refreshToken = " + tokenPair.refreshToken());
        List<User> users = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = userRepository.save(User.create(adjectives.get(i).getAdjective(), "https://t1.kakaocdn.net/account_images/default_profile.jpeg"));
            users.add(user);
            for (int j = 0; j < 30; j += 2) {
                ImageFile imageFile1 = imageFileRepository.save(ImageFile.create(new ImageFileDto("202502240006030.png", "https://image.photopic.site/images-dev/202502240006030.png", "https://image.photopic.site/images-dev/resized_202502240006030.png")));
                ImageFile imageFile2 = imageFileRepository.save(ImageFile.create(new ImageFileDto("202502240006030.png", "https://image.photopic.site/images-dev/202502240006030.png", "https://image.photopic.site/images-dev/resized_202502240006030.png")));
                posts.add(postRepository.save(Post.create(user.getId(), "description" + j, List.of(PostImage.create("뽀또A", imageFile1.getId()), PostImage.create("뽀또B", imageFile2.getId())), "https://photopic.site/shareurl")));
            }
        }
        for (User user : users) {
            for (Post post : posts) {
                Random random = new Random();
                int num = random.nextInt(2);
                voteService.vote(user.getId(), post.getId(), post.getImages().get(num).getId());
            }
        }
    }
}
