package com.dalal.notificationsservicepfe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${public-key}")
    private String publicKeyString;

    private PublicKey publicKey;

    // c'est très important pour éviter la répétition
    @PostConstruct
    public void init() throws Exception {
        this.publicKey = getPublicKeyFromString();
    }

    private PublicKey getPublicKeyFromString() throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyString.trim());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public String extractUsername(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractId(String token) throws Exception {
        return extractClaim(token, claims -> {
            Object id = claims.get("id");
            if (id instanceof Number number) {
                return number.longValue();
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) throws Exception {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public <R> R extractClaim(String token, Function<Claims, R> claimsResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(this.publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}