package com.swyp8team2.auth.presentation.filter;

import com.swyp8team2.auth.application.jwt.JwtClaim;
import com.swyp8team2.auth.application.jwt.JwtProvider;
import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.common.exception.ApplicationException;
import com.swyp8team2.user.domain.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.swyp8team2.auth.presentation.filter.JwtAuthenticationEntryPoint.EXCEPTION_KEY;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final HeaderTokenExtractor headerTokenExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            JwtClaim claim = jwtProvider.parseToken(headerTokenExtractor.extractToken(authorization));

            Authentication authentication = getAuthentication(claim.idAsLong());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ApplicationException e) {
            request.setAttribute(EXCEPTION_KEY, e);
        } finally {
            doFilter(request, response, filterChain);
        }
    }

    private Authentication getAuthentication(long userId) {
        UserInfo userInfo = new UserInfo(userId, Role.USER);
        return new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities());
    }
}
