package com.swyp8team2.support;

import com.swyp8team2.common.config.JpaConfig;
import com.swyp8team2.common.config.QueryDslConfig;
import com.swyp8team2.post.infrastructure.PostQueryRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({
        JpaConfig.class,
        QueryDslConfig.class,
        PostQueryRepository.class
})
@DataJpaTest
public abstract class RepositoryTest {
}
