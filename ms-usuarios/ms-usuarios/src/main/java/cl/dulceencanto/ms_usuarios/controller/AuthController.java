package cl.dulceencanto.ms_usuarios.controller;

import cl.dulceencanto.ms_usuarios.dto.LoginRequest;
import cl.dulceencanto.ms_usuarios.dto.RegistroRequest;
import cl.dulceencanto.ms_usuarios.dto.UsuarioResponse;
import cl.dulceencanto.ms_usuarios.model.Usuario;
import cl.dulceencanto.ms_usuarios.repository.UsuarioRepository;
import cl.dulceencanto.ms_usuarios.security.JwtService;
import cl.dulceencanto.ms_usuarios.service.UsuarioService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro público, login y renovación de tokens JWT (no requieren token)")
@SecurityRequirements // estos endpoints son públicos: Swagger no pide candado
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository, UsuarioService usuarioService,
                          PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Registrar cliente",
            description = "Crea una cuenta con rol CLIENTE. El rol NO se puede elegir desde este endpoint.")
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarCliente(req));
    }

    @Operation(summary = "Iniciar sesión",
            description = "Valida correo y contraseña (BCrypt) y devuelve accessToken (15 min) y refreshToken (7 días).")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        Usuario usuario = usuarioRepository.findByCorreo(req.correo().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(req.password(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", usuario.getId());
        userInfo.put("nombre", usuario.getNombre());
        userInfo.put("correo", usuario.getCorreo());
        userInfo.put("rol", usuario.getRol());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("accessToken", jwtService.generarAccessToken(usuario.getId(), usuario.getCorreo(), usuario.getRol()));
        response.put("refreshToken", jwtService.generarRefreshToken(usuario.getCorreo()));
        response.put("usuario", userInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Renovar access token",
            description = "Recibe un refreshToken vigente y devuelve un nuevo accessToken con el rol actual del usuario.")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtService.esTokenValido(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido o expirado");
        }
        Claims claims;
        try {
            claims = jwtService.extraerClaims(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido o expirado");
        }
        if (!"REFRESH".equals(claims.get("tipo", String.class))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tipo de token inválido");
        }
        Usuario usuario = usuarioRepository.findByCorreo(claims.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        return ResponseEntity.ok(Map.of(
                "accessToken", jwtService.generarAccessToken(usuario.getId(), usuario.getCorreo(), usuario.getRol())));
    }
}
