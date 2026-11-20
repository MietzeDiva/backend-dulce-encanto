package cl.dulceencanto.ms_productos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer stock;

    // Según el requerimiento, por ahora la categoría es solo un texto
    @Column(nullable = false, length = 50)
    private String categoria; 

    private String imagenUrl;
}