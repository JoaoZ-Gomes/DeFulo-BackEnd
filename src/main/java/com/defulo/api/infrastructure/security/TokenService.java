package com.defulo.api.infrastructure.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.defulo.api.features.usuario.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.defulo.api.infrastructure.exception.InvalidTokenException;

@Service
public class TokenService {

    @Value("${api.security.token.secret:my-secret-key}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setIssuer("DeFulo API")
                .setSubject(usuario.getEmail())
                .setExpiration(gerarDataExpiracao())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getSubject(String tokenJWT) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(tokenJWT)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new InvalidTokenException("Token JWT inválido ou expirado!");
        }
    }

    private java.util.Date gerarDataExpiracao() {
        return java.util.Date.from(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")));
    }
}
