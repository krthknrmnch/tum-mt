package com.tum.in.cm.platformservice.component.security;

import com.tum.in.cm.platformservice.exception.CustomAuthException;
import com.tum.in.cm.platformservice.model.user.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {
    public static String fetchToken() throws CustomAuthException {
        AnonymousAuthenticationToken authentication = (AnonymousAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new CustomAuthException("");
        User user = (User) authentication.getPrincipal();
        return user.getEmail();
    }
}
