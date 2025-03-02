package com.swyp8team2.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    @Profile("prod")
    public UrlBasedCorsConfigurationSource corsConfigurationSourceProd() {
        CorsConfiguration configuration = getCorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://photopic.site"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Profile({"local", "dev", "default", "test"})
    public UrlBasedCorsConfigurationSource corsConfigurationSourceLocal() {
        CorsConfiguration configuration = getCorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://dev.photopic.site"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of("GET","POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        return configuration;
    }
}
