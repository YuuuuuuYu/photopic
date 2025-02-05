package com.swyp8team2.common.config;

import com.swyp8team2.auth.application.JwtProvider;
import com.swyp8team2.auth.application.OAuthService;
import com.swyp8team2.auth.presentation.filter.HeaderTokenExtractor;
import com.swyp8team2.auth.presentation.filter.JwtAuthFilter;
import com.swyp8team2.auth.presentation.filter.JwtAuthenticationEntryPoint;
import com.swyp8team2.auth.presentation.filter.OAuthLoginFailureHandler;
import com.swyp8team2.auth.presentation.filter.OAuthLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuthService oAuthService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(
            OAuthService oAuthService,
            OAuthLoginSuccessHandler oAuthLoginSuccessHandler,
            OAuthLoginFailureHandler oAuthLoginFailureHandler,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.oAuthService = oAuthService;
        this.oAuthLoginSuccessHandler = oAuthLoginSuccessHandler;
        this.oAuthLoginFailureHandler = oAuthLoginFailureHandler;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/",
                        "/error",
                        "/favicon.ico",
                        "/index.html",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/favicon.ico"
                );
    }

    @Bean
    @Profile({"dev", "local"})
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            HandlerMappingIntrospector introspect,
            JwtProvider jwtProvider
    ) throws Exception {
        MvcRequestMatcher[] matchers = getWhiteList(introspect);
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        matchers
                                ).permitAll()
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .anyRequest().authenticated()
                )
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(
                        new JwtAuthFilter(jwtProvider, new HeaderTokenExtractor()),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint(handlerExceptionResolver))
                )

                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                                .successHandler(oAuthLoginSuccessHandler)
                                .failureHandler(oAuthLoginFailureHandler));

        return http.build();
    }

    private static MvcRequestMatcher[] getWhiteList(HandlerMappingIntrospector introspect) {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspect);
        return new MvcRequestMatcher[]{
                mvc.pattern("/auth/reissue"),
                mvc.pattern("/guest")
        };
    }
}
