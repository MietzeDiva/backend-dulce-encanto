package cl.dulceencanto.ms_usuarios.config;

import cl.dulceencanto.ms_usuarios.model.Usuario;
import cl.dulceencanto.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea cuentas de demostración (admin, vendedor, cliente) si no existen.
 * Se desactiva con APP_SEED_ENABLED=false. La contraseña sale de APP_SEED_PASSWORD (.env).
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final String password;

    public DataLoader(UsuarioRepository repo, PasswordEncoder encoder,
                      @Value("${app.seed.password:Dulce2026!}") String password) {
        this.repo = repo;
        this.encoder = encoder;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        crear("Administrador", "admin@dulceencanto.cl", "ADMIN");
        crear("Vendedor Demo", "vendedor@dulceencanto.cl", "VENDEDOR");
        crear("Cliente Demo", "cliente@dulceencanto.cl", "CLIENTE");
    }

    private void crear(String nombre, String correo, String rol) {
        if (repo.existsByCorreo(correo)) return;
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setPassword(encoder.encode(password));
        u.setRol(rol);
        repo.save(u);
    }
}
