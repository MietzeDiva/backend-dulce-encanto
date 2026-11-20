package cl.dulceencanto.ms_ventas.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "boletas")
public class Boleta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId; // Guarda el ID del usuario de ms-usuarios

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false, length = 20)
    private String estado = "COMPLETADA"; // COMPLETADA o ANULADA

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetalleBoleta> detalles;
}