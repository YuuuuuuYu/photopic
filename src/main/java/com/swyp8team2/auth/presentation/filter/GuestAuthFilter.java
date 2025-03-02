package com.swyp8team2.auth.presentation.filter;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.common.annotation.GuestTokenCryptoService;
import com.swyp8team2.common.exception.ApplicationException;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.crypto.application.CryptoService;
import com.swyp8team2.user.domain.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static com.swyp8team2.auth.presentation.filter.JwtAuthenticationEntryPoint.EXCEPTION_KEY;

@Slf4j
public class GuestAuthFilter extends OncePerRequestFilter {

    private final CryptoService cryptoService;

    public GuestAuthFilter(@GuestTokenCryptoService CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            AntPathMatcher matcher = new AntPathMatcher();
            if (!matcher.match("/posts/{postId}/votes/guest", request.getRequestURI())) {
                return;
            }
            String token = request.getHeader(CustomHeader.GUEST_TOKEN);
            if (Objects.isNull(token)) {
                throw new BadRequestException(ErrorCode.INVALID_GUEST_HEADER);
            }
            String guestId = cryptoService.decrypt(token);
            Authentication authentication = getAuthentication(Long.parseLong(guestId));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ApplicationException e) {
            request.setAttribute(EXCEPTION_KEY, e);
        } catch (Exception e) {
            log.debug("GuestAuthFilter error", e);
            request.setAttribute(EXCEPTION_KEY, new BadRequestException(ErrorCode.INVALID_TOKEN));
        } finally {
            doFilter(request, response, filterChain);
        }
    }

    private Authentication getAuthentication(long userId) {
        UserInfo userInfo = new UserInfo(userId, Role.GUEST);
        return new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities());
    }
}
