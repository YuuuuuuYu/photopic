package com.swyp8team2.auth.presentation.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp8team2.common.exception.ApplicationException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.ErrorResponse;
import com.swyp8team2.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver
    ) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Exception e = (Exception) request.getAttribute("exception");

        if (Objects.nonNull(e)) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
}
