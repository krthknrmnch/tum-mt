package com.tum.in.cm.platformservice.component.security;

import com.tum.in.cm.platformservice.util.Constants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.tum.in.cm.platformservice.util.Constants.*;

@Component
public class JwtBuilder {
    public String buildJwtForUser(String email, String JWT_SECRET) {
        return this.buildJwtWithRole(email, Constants.ROLE_AUTHORIZED_USER, JWT_SECRET);
    }

    public String buildJwtForAdmin(String email, String JWT_SECRET) {
        return this.buildJwtWithRole(email, Constants.ROLE_AUTHORIZED_ADMIN, JWT_SECRET);
    }

    /***
     * Build a JWT for the given role
     */
    private String buildJwtWithRole(String email, String role, String JWT_SECRET) {
        byte[] signingKey = JWT_SECRET.getBytes();
        return Jwts.builder().signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam(TOKEN_CLAIM_TYPE, TOKEN_TYPE)
                .setIssuer(TOKEN_ISSUER)
                .setSubject(email)
                .claim(TOKEN_CLAIM_ROLE, role)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRY_MS)).compact();
    }
}
