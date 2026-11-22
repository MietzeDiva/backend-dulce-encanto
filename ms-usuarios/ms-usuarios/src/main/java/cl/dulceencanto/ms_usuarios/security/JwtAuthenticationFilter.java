package cl.dulceencanto.ms_usuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // parseSignedClaims valida firma Y expiración; lanza JwtException si algo falla
                Claims claims = jwtService.extraerClaims(authHeader.substring(7));
                String rol = claims.get("rol", String.class);
                // Solo los ACCESS tokens autentican; un REFRESH token no sirve para llamar a la API
                if ("ACCESS".equals(claims.get("tipo", String.class)) && claims.getSubject() != null && rol != null) {
                    var authority = new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase());
                    var auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, List.of(authority));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // Token inválido/expirado: sin autenticación → el EntryPoint responde 401
            }
        }
        filterChain.doFilter(request, response);
    }
}
