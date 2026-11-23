package cl.dulceencanto.ms_usuarios.config;

import cl.dulceencanto.ms_usuarios.model.Usuario;
import cl.dulceencanto.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final String passAdmin;
    private final String passVendedor;
    private final String passCliente;

    public DataLoader(UsuarioRepository repo, PasswordEncoder encoder,
                      @Value("${app.seed.admin-password}") String passAdmin,
                      @Value("${app.seed.vendedor-password}") String passVendedor,
                      @Value("${app.seed.cliente-password}") String passCliente) {
        this.repo = repo;
        this.encoder = encoder;
        this.passAdmin = passAdmin;
        this.passVendedor = passVendedor;
        this.passCliente = passCliente;
    }

    @Override
    public void run(String... args) {
        crear("Administrador", "admin@dulceencanto.cl", passAdmin, "ADMIN");
        crear("Vendedor Demo", "vendedor@dulceencanto.cl", passVendedor, "VENDEDOR");
        crear("Cliente Demo", "cliente@dulceencanto.cl", passCliente, "CLIENTE");
    }

    private void crear(String nombre, String correo, String password, String rol) {
        if (repo.existsByCorreo(correo)) return;
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setPassword(encoder.encode(password));
        u.setRol(rol);
        repo.save(u);
    }
}