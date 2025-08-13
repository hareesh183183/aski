package com.angidi.aski.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTService {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JWTService.class);
    private static String key = "";

    public JWTService() throws NoSuchAlgorithmException {
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sKey = keyGen.generateKey();
            key = Base64.getEncoder().encodeToString(sKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("JWTService()", e);
            throw e;
        }

    }

    public record JWTResponse(String token, Date expiration) {}

    public JWTResponse generateJwtToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        String token = createWebToken(claims, email);
        return new JWTResponse(token, extractExpiration(token));
    }

    public SecretKey getKey(){
         byte [] keys =  Base64.getDecoder().decode(key);
         return Keys.hmacShaKeyFor(keys);
    }

    public String createWebToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60*60*30))
                .signWith(getKey())
                .compact();
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token,  Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        return extractUsername(token).equals(userDetails.getUsername()) &&
        ! isTokenExpired(token);
    }
}
