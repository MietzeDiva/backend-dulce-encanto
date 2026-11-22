package cl.dulceencanto.ms_productos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 80)
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Size(max = 255)
    @Column(length = 255)
    private String descripcion;
}
