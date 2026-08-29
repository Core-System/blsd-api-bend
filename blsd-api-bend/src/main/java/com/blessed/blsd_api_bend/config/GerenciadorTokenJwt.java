package com.blessed.blsd_api_bend.config;

import com.blessed.blsd_api_bend.dto.usuario.UsuarioDetalheDTO;
import com.blessed.blsd_api_bend.model.entity.Acesso;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GerenciadorTokenJwt {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.validity}")
    private long jwtTokenValidity;

    public String getUsernameFromToken(String token) {
        return getClaimForToken(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        return getClaimForToken(token, claims -> claims.get("role", String.class));
    }

    public Long getIdFromToken(String token) {
        return getClaimForToken(token, claims -> {
            Number id = claims.get("id", Number.class);
            return id != null ? id.longValue() : null;
        });
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimForToken(token, Claims::getExpiration);
    }

    public String generateToken(final Authentication authentication) {
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.joining(","));

        Long usuarioId = null;
        if (authentication.getPrincipal() instanceof UsuarioDetalheDTO) {
            usuarioId = ((UsuarioDetalheDTO) authentication.getPrincipal()).getId();
        }

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("role", authorities)
                .claim("id", usuarioId)
                .signWith(parseSecret())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1_000))
                .compact();
    }

    public String generateToken(String username, Acesso acesso, Long usuarioId) {
        final String role = acesso.getNome().toString().replace("ROLE_", "");

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("id", usuarioId)
                .signWith(parseSecret())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1_000))
                .compact();
    }

    public <T> T getClaimForToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    protected Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(parseSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey parseSecret() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}