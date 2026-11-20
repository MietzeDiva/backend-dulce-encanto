package cl.dulceencanto.ms_usuarios.model;

import jakarta.persistence.*;
import lombok.Data;

@Data // Anotación de Lombok que nos ahorra escribir getters y setters
@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String rol; // ADMIN, VENDEDOR, CLIENTE
}