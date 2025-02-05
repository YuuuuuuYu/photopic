package com.swyp8team2.auth.presentation.filter;

import com.swyp8team2.auth.application.JwtClaim;
import com.swyp8team2.auth.application.JwtProvider;
import com.swyp8team2.common.exception.ApplicationException;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            JwtClaim claim = jwtProvider.parseToken(headerTokenExtractor.extractToken(authorization));

            User user = userRepository.findById(claim.idAsLong())
                    .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

            Authentication authentication = getAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ApplicationException e) {
            request.setAttribute("exception", e);
        } finally {
            doFilter(request, response, filterChain);
        }
    }

    private Authentication getAuthentication(User user) {
        List<GrantedAuthority> authorities = Collections.emptyList();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getNickname())
                .password("")
                .authorities(authorities)
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}
