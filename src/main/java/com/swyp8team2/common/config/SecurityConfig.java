package com.swyp8team2.common.config;

import com.swyp8team2.auth.application.jwt.JwtProvider;
import com.swyp8team2.auth.presentation.filter.CustomAccessDenialHandler;
import com.swyp8team2.auth.presentation.filter.HeaderTokenExtractor;
import com.swyp8team2.auth.presentation.filter.JwtAuthFilter;
import com.swyp8team2.auth.presentation.filter.JwtAuthenticationEntryPoint;
import com.swyp8team2.user.domain.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/",
                        "/error",
                        "/index.html",
                        "/css/**",
                        "/images/**",
                        "/js/**",
                        "/favicon.ico",
                        "/docs/**",
                        "/actuator/health"
                );
    }

    @Bean
    @Profile("local")
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            HandlerMappingIntrospector introspect,
            JwtProvider jwtProvider,
            UrlBasedCorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(getWhiteList(introspect)).permitAll()
                                .requestMatchers(getGuestAllowedList(introspect))
                                .hasAnyRole(Role.USER.name(), Role.GUEST.name())
                                .anyRequest().hasRole(Role.USER.name()))

                .addFilterBefore(
                        new JwtAuthFilter(jwtProvider, new HeaderTokenExtractor()),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                        new JwtAuthenticationEntryPoint(handlerExceptionResolver))
                                .accessDeniedHandler((request, response, accessDeniedException) ->
                                        new CustomAccessDenialHandler(handlerExceptionResolver)
                                                .handle(request, response, accessDeniedException)
                                )
                );
        return http.build();
    }

    public static MvcRequestMatcher[] getWhiteList(HandlerMappingIntrospector introspect) {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspect);
        return new MvcRequestMatcher[]{
                mvc.pattern(HttpMethod.POST, "/auth/oauth2/code/kakao"),
                mvc.pattern(HttpMethod.POST, "/auth/guest/sign-in"),
                mvc.pattern(HttpMethod.POST, "/auth/reissue"),
                mvc.pattern(HttpMethod.GET, "/posts/shareUrl/{shareUrl}"),
                mvc.pattern(HttpMethod.GET, "/posts/{postId}/comments"),
        };
    }

    public static MvcRequestMatcher[] getGuestAllowedList(HandlerMappingIntrospector introspect) {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspect);
        return new MvcRequestMatcher[]{
                mvc.pattern(HttpMethod.POST, "/posts/{postId}/votes"),
                mvc.pattern(HttpMethod.DELETE, "/votes/{voteId}"),
                mvc.pattern(HttpMethod.GET, "/users/me"),
        };
    }
}
