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
import java.util.Objects;

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
    private Status status;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostImage> images = new ArrayList<>();

    private String shareUrl;

    private VoteType voteType;

    public Post(
            Long id,
            Long userId,
            String description,
            Status status,
            Scope scope,
            List<PostImage> images,
            String shareUrl,
            VoteType voteType
    ) {
        validateDescription(description);
        validatePostImages(images);
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.status = status;
        this.scope = scope;
        this.images = images;
        images.forEach(image -> image.setPost(this));
        this.shareUrl = shareUrl;
        this.voteType = voteType;
    }

    private void validatePostImages(List<PostImage> images) {
        if (images.size() < 2 || images.size() > 9) {
            throw new BadRequestException(ErrorCode.INVALID_POST_IMAGE_COUNT);
        }
    }

    private void validateDescription(String description) {
        if (description.length() > 100) {
            throw new BadRequestException(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED);
        }
    }

    public static Post create(Long userId, String description, List<PostImage> images, VoteType voteType) {
        return new Post(null, userId, description, Status.PROGRESS, Scope.PRIVATE, images, null, voteType);
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
        if (!isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        if (status == Status.CLOSED) {
            throw new BadRequestException(ErrorCode.POST_ALREADY_CLOSED);
        }
        this.status = Status.CLOSED;
    }

    public boolean isAuthor(Long userId) {
        return this.userId.equals(userId);
    }

    public void validateProgress() {
        if (!this.status.equals(Status.PROGRESS)) {
            throw new BadRequestException(ErrorCode.POST_ALREADY_CLOSED);
        }
    }

    public void setShareUrl(String shareUrl) {
        if (Objects.nonNull(this.shareUrl)) {
            throw new InternalServerException(ErrorCode.SHARE_URL_ALREADY_EXISTS);
        }
        this.shareUrl = shareUrl;
    }

    public void toggleScope(Long userId) {
        if (!isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        this.scope = scope.equals(Scope.PRIVATE) ? Scope.PUBLIC : Scope.PRIVATE;
    }
}
