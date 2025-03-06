package com.swyp8team2.post.infrastructure;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp8team2.common.util.QueryDslUtil;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.swyp8team2.common.util.QueryDslUtil.*;
import static com.swyp8team2.common.util.QueryDslUtil.ltCursor;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final JPAQueryFactory factory;

    /**
     * SELECT p
     * FROM Post p
     * WHERE p.userId = :userId
     * AND (:cursor IS NULL OR p.id < :cursor)
     * ORDER BY p.id DESC
     **/
    Slice<Post> findByUserId(Long userId, Long cursor, Pageable pageable) {
        List<Post> postList = factory.selectFrom(QPost.post)
                .where(QPost.post.userId.eq(userId)
                        .and(ltCursor(cursor, QPost.post.id)))
                .orderBy(QPost.post.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        boolean hasNext = removeLastIfHasNext(pageable.getPageSize(), postList);
        return new SliceImpl<>(postList, pageable, hasNext);
    }
}
