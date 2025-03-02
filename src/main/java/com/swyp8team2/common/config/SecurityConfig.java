package com.swyp8team2.common.config;

import com.swyp8team2.auth.application.jwt.JwtProvider;
import com.swyp8team2.auth.presentation.filter.GuestAuthFilter;
import com.swyp8team2.auth.presentation.filter.HeaderTokenExtractor;
import com.swyp8team2.auth.presentation.filter.JwtAuthFilter;
import com.swyp8team2.auth.presentation.filter.JwtAuthenticationEntryPoint;
import com.swyp8team2.common.annotation.GuestTokenCryptoService;
import com.swyp8team2.crypto.application.CryptoService;
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
    private final CryptoService cryptoService;

    public SecurityConfig(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
            @GuestTokenCryptoService CryptoService cryptoService
    ) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.cryptoService = cryptoService;
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
                                .requestMatchers(getGuestTokenRequestList(introspect))
                                .hasAnyRole(Role.USER.name(), Role.GUEST.name())
                                .anyRequest().authenticated())

                .addFilterBefore(
                        new JwtAuthFilter(jwtProvider, new HeaderTokenExtractor()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(
                        new GuestAuthFilter(cryptoService),
                        JwtAuthFilter.class
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint(handlerExceptionResolver)));
        return http.build();
    }

    public static MvcRequestMatcher[] getWhiteList(HandlerMappingIntrospector introspect) {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspect);
        return new MvcRequestMatcher[]{
                mvc.pattern("/auth/reissue"),
                mvc.pattern("/auth/guest/token"),
                mvc.pattern(HttpMethod.GET, "/posts/shareUrl/{shareUrl}"),
                mvc.pattern(HttpMethod.GET, "/posts/{postId}"),
                mvc.pattern(HttpMethod.GET, "/posts/{postId}/comments"),
                mvc.pattern("/auth/oauth2/**"),
        };
    }

    public static MvcRequestMatcher[] getGuestTokenRequestList(HandlerMappingIntrospector introspect) {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspect);
        return new MvcRequestMatcher[]{
                mvc.pattern("/posts/{postId}/votes/guest"),
        };
    }
}
