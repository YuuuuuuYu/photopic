package com.swyp8team2.common.dev;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"dev", "local"})
@Component
@RequiredArgsConstructor
public class DataInitConfig {

    private final DataInitializer dataInitializer;

    @PostConstruct
    public void init() {
        dataInitializer.init();
    }
}
