package com.swyp8team2.auth.presentation.filter;

import com.swyp8team2.common.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Objects;

public class CustomAccessDenialHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver exceptionResolver;

    public CustomAccessDenialHandler(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver
    ) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) {
        if (Objects.nonNull(accessDeniedException)) {
            exceptionResolver.resolveException(request, response, null, new ForbiddenException());
        }
    }
}
