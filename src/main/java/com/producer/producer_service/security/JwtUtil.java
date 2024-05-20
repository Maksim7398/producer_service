package com.producer.producer_service.security;

import com.producer.producer_service.exception.NotAuthorizedException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public String extractProfileID(String token) {
        return extractClaim(token, c -> c.get("profileId")).toString();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public void validateToken(String token, String userId) {
        String profileID = extractProfileID(token);
        if (profileID.equals(userId) && !isTokenExpired(token)) {
            return;
        }
        throw new NotAuthorizedException("Not authorized");
    }
}
