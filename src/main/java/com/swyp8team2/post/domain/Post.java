package com.swyp8team2.post.domain;

import com.swyp8team2.common.domain.BaseEntity;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.swyp8team2.common.util.Validator.*;

@Getter
@Entity
@ToString(exclude = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostImage> images = new ArrayList<>();

    private String shareUrl;

    public Post(Long id, Long userId, String description, State state, List<PostImage> images, String shareUrl) {
        validateDescription(description);
        validatePostImages(images);
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.state = state;
        this.images = images;
        images.forEach(image -> image.setPost(this));
        this.shareUrl = shareUrl;
    }

    private void validatePostImages(List<PostImage> images) {
        if (images.size() < 2) {
            throw new BadRequestException(ErrorCode.INVALID_POST_IMAGE_COUNT);
        }
    }

    private void validateDescription(String description) {
        if (description.length() > 100) {
            throw new BadRequestException(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED);
        }
    }

    public static Post create(Long userId, String description, List<PostImage> images, String shareUrl) {
        return new Post(null, userId, description, State.PROGRESS, images, shareUrl);
    }

    public PostImage getBestPickedImage() {
        return images.stream()
                .max(Comparator.comparing(PostImage::getVoteCount))
                .orElseThrow(() -> new InternalServerException(ErrorCode.POST_IMAGE_NOT_FOUND));
    }

    public void vote(Long imageId) {
        PostImage image = images.stream()
                .filter(postImage -> postImage.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_IMAGE_NOT_FOUND));
        image.increaseVoteCount();
    }

    public void cancelVote(Long imageId) {
        PostImage image = images.stream()
                .filter(postImage -> postImage.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new InternalServerException(ErrorCode.POST_IMAGE_NOT_FOUND));
        image.decreaseVoteCount();
    }

    public void close(Long userId) {
        validateOwner(userId);
        if (state == State.CLOSED) {
            throw new BadRequestException(ErrorCode.POST_ALREADY_CLOSED);
        }
        this.state = State.CLOSED;
    }

    public void validateOwner(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
    }
}
