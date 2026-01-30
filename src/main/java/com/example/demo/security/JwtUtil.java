package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey signingKey;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        if (secret != null && secret.length() >= 32) {
            this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        } else {
            this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.out.println("⚠️ WARNING: Using auto-generated JWT key. For production, set jwt.secret in application.properties");
            System.out.println("Generated Key (Base64): " + Base64.getEncoder().encodeToString(signingKey.getEncoded()));
        }
    }

    // ✅ Generate token with email as subject
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract email (subject) from token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ ADDED: Extract expiration date
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ Generic method to extract any claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ✅ Validate token
    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }
}