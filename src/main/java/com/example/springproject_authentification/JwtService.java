package com.example.springproject_authentification;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    // Reads the secret key from application.properties.
    @Value("${jwt.secret}")
    private String secret;

    // Reads token lifetime in milliseconds from application.properties.
    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user){
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role",user.getRole().name())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Token is valid when it belongs to the expected user and is not expired.
    public boolean isTokenValid(String token, User user) {
        try {
            String username = extractUsername(token);
            return username.equals(user.getUsername()) && !isTokenExpired(token);
        } catch (Exception exception) {
            // Any parse/signature error means the token is not valid.
            return false;
        }
    }

    // Checks whether the token expiration time is already in the past.
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Reads only the expiration date from the token.
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic helper for reading one piece of information from the token claims.
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parses the token, verifies the signature, and returns all claims.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Builds the signing key object from the Base64 secret string.
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
