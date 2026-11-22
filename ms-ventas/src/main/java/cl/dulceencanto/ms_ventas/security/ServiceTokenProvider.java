package cl.dulceencanto.ms_ventas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Token de servicio para que ms-ventas hable con ms-productos (descontar/reponer stock).
 * Se firma con el mismo JWT_SECRET, dura 2 minutos y lleva rol SERVICIO. Ese rol no se puede
 * asignar a ningún usuario (ms-usuarios solo acepta ADMIN, VENDEDOR y CLIENTE).
 */
@Component
public class ServiceTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String generar() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .subject("ms-ventas")
                .claim("rol", "SERVICIO")
                .claim("tipo", "ACCESS")
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + 2 * 60 * 1000))
                .signWith(key)
                .compact();
    }
}
