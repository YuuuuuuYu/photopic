package com.swyp8team2.support;

import com.swyp8team2.common.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaConfig.class)
@DataJpaTest
public abstract class RepositoryTest {
}
