package com.tum.in.cm.platformservice.component.security;

import com.tum.in.cm.platformservice.exception.CustomNotFoundException;
import com.tum.in.cm.platformservice.model.user.User;
import com.tum.in.cm.platformservice.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.tum.in.cm.platformservice.util.Constants.*;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    ServletContext servletContext;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (servletContext == null) {
            servletContext = request.getServletContext();
        }
        if (userService == null) {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            userService = Objects.requireNonNull(webApplicationContext).getBean(UserService.class);
        }
        if (environment == null) {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            environment = Objects.requireNonNull(webApplicationContext).getBean(Environment.class);
        }
        String authHeader = request.getHeader(TOKEN_HEADER);
        if (!StringUtils.hasLength(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(authHeader));
        filterChain.doFilter(request, response);
    }

    /**
     * Gets an anon authentication for given token
     */
    public AnonymousAuthenticationToken getAuthentication(String token) {
        AnonymousAuthenticationToken anonymousAuthenticationToken;
        final String JWT_SECRET = environment.getProperty("auth.jwt.secret");
        try {
            byte[] signingKey = JWT_SECRET.getBytes();
            Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token.replace("Bearer ", "")).getBody();
            String email = claims.getSubject();

            String role = (String) claims.get(TOKEN_CLAIM_ROLE);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>(List.of(authority));

            //Don't give authorization for the following cases:
            //User does not exist
            if (!userService.existsByEmail(email))
                return null;
            //Else
            User user = userService.findByEmail(email);
            anonymousAuthenticationToken = new AnonymousAuthenticationToken(email, user, authorities);
        } catch (ExpiredJwtException exception) {
            log.error("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
            throw new AccessDeniedException(LOGIN_FAILED_MSG);
        } catch (UnsupportedJwtException exception) {
            log.error("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
            throw new AccessDeniedException(LOGIN_FAILED_MSG);
        } catch (MalformedJwtException exception) {
            log.error("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
            throw new AccessDeniedException(LOGIN_FAILED_MSG);
        } catch (SignatureException exception) {
            log.error("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
            throw new AccessDeniedException(LOGIN_FAILED_MSG);
        } catch (IllegalArgumentException exception) {
            log.error("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
            throw new AccessDeniedException(LOGIN_FAILED_MSG);
        } catch (CustomNotFoundException e) {
            log.error(USER_NOT_FOUND_MSG);
            throw new AccessDeniedException(LOGIN_FAILED_MSG);
        }
        return anonymousAuthenticationToken;
    }
}
