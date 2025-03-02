package com.swyp8team2.common.config;

import com.swyp8team2.auth.application.oauth.KakaoOAuthClient;
import com.swyp8team2.common.exception.DiscordClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public KakaoOAuthClient kakaoAuthClient() {
        RestClientAdapter adapter = RestClientAdapter.create(RestClient.create());
        HttpServiceProxyFactory build = HttpServiceProxyFactory
                .builderFor(adapter).build();
        return build.createClient(KakaoOAuthClient.class);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
