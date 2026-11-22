package cl.dulceencanto.ms_usuarios.service;

import cl.dulceencanto.ms_usuarios.dto.RegistroRequest;
import cl.dulceencanto.ms_usuarios.dto.UsuarioRequest;
import cl.dulceencanto.ms_usuarios.dto.UsuarioResponse;
import cl.dulceencanto.ms_usuarios.model.Usuario;
import cl.dulceencanto.ms_usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    public static final Set<String> ROLES = Set.of("ADMIN", "VENDEDOR", "CLIENTE");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.from(buscar(id));
    }

    /** Registro público: el rol SIEMPRE es CLIENTE, no importa lo que mande el cliente HTTP. */
    public UsuarioResponse registrarCliente(RegistroRequest req) {
        return guardarNuevo(req.nombre(), req.correo(), req.password(), "CLIENTE");
    }

    /** Alta desde el panel de administración (solo ADMIN, lo exige SecurityConfig). */
    public UsuarioResponse crear(UsuarioRequest req) {
        if (req.password() == null || req.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        return guardarNuevo(req.nombre(), req.correo(), req.password(), validarRol(req.rol()));
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest req) {
        Usuario u = buscar(id);
        String correo = normalizar(req.correo());
        if (!correo.equals(u.getCorreo()) && usuarioRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        u.setNombre(req.nombre().trim());
        u.setCorreo(correo);
        u.setRol(validarRol(req.rol()));
        if (req.password() != null && !req.password().isBlank()) {
            u.setPassword(passwordEncoder.encode(req.password()));
        }
        return UsuarioResponse.from(usuarioRepository.save(u));
    }

    /** Un administrador no puede eliminar su propia cuenta (evita quedarse sin acceso). */
    public void eliminar(Long id, String correoSolicitante) {
        Usuario u = buscar(id);
        if (u.getCorreo().equalsIgnoreCase(correoSolicitante)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No puedes eliminar tu propia cuenta");
        }
        usuarioRepository.delete(u);
    }

    private UsuarioResponse guardarNuevo(String nombre, String correo, String password, String rol) {
        String correoNormal = normalizar(correo);
        if (usuarioRepository.existsByCorreo(correoNormal)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario u = new Usuario();
        u.setNombre(nombre.trim());
        u.setCorreo(correoNormal);
        u.setPassword(passwordEncoder.encode(password));
        u.setRol(rol);
        return UsuarioResponse.from(usuarioRepository.save(u));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private String validarRol(String rol) {
        String r = rol == null ? "" : rol.trim().toUpperCase();
        if (!ROLES.contains(r)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido (ADMIN, VENDEDOR o CLIENTE)");
        }
        return r;
    }

    private String normalizar(String correo) {
        return correo.trim().toLowerCase();
    }
}
