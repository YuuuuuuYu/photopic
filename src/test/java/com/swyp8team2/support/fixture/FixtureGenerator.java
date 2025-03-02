package com.swyp8team2.support.fixture;

import com.swyp8team2.image.domain.ImageFile;
import com.swyp8team2.image.presentation.dto.ImageFileDto;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostImage;
import com.swyp8team2.user.domain.User;

import java.util.List;

public abstract class FixtureGenerator {

    public static Post createPost(Long userId, ImageFile imageFile1, ImageFile imageFile2, int key) {
        return Post.create(
                userId,
                "description" + key,
                List.of(
                        PostImage.create("뽀또A", imageFile1.getId()),
                        PostImage.create("뽀또B", imageFile2.getId())
                )
        );
    }

    public static User createUser(int key) {
        return User.create("nickname" + key, "profileUrl" + key);
    }

    public static ImageFile createImageFile(int key) {
        return ImageFile.create(
                new ImageFileDto(
                        "originalFileName" + key,
                        "imageUrl" + key,
                        "thumbnailUrl" + key
                )
        );
    }
}
