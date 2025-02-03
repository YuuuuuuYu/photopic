package com.swyp8team2.common.config;

import com.swyp8team2.auth.application.OAuthService;
import com.swyp8team2.auth.presentation.filter.JwtAuthFilter;
import com.swyp8team2.auth.presentation.filter.JwtExceptionFilter;
import com.swyp8team2.auth.presentation.filter.OAuthLoginFailureHandler;
import com.swyp8team2.auth.presentation.filter.OAuthLoginSuccessHandler;
import com.swyp8team2.common.filter.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final OAuthService oAuthService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/", "/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspect) throws Exception {
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspect);

        MvcRequestMatcher[] permitWhiteList = {
                mvc.pattern("/auth/sign-in"),
                mvc.pattern("/auth/sign-up"),
                mvc.pattern("/auth/refresh"),
        };
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(permitWhiteList).permitAll()
                                .requestMatchers("/","/index.html", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class)
                .exceptionHandling(e -> e.accessDeniedHandler(new CustomAccessDeniedHandler()))

                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                                .successHandler(oAuthLoginSuccessHandler)
                                .failureHandler(oAuthLoginFailureHandler));

        return http.build();
    }
}
