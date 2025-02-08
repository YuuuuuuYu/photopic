package com.swyp8team2.auth.presentation.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String EXCEPTION_KEY = "exception";

    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver
    ) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        Exception e = (Exception) request.getAttribute(EXCEPTION_KEY);

        if (Objects.nonNull(e)) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
}
