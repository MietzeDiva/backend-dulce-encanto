package cl.dulceencanto.ms_ventas.dto;

import lombok.Data;

@Data
public class ProductoExternalDTO {
    private Long id;
    private String nombre;
    private Integer precio;
    private Integer precioOferta;
    private Integer stock;
}