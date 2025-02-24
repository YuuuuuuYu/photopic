package com.swyp8team2.support;

import com.swyp8team2.common.config.CommonConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(CommonConfig.class)
public abstract class RepositoryTest {
}
