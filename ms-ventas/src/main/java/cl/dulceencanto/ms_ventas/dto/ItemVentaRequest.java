package cl.dulceencanto.ms_ventas.dto;

import lombok.Data;

@Data
public class ItemVentaRequest {
    private Long productoId;
    private Integer cantidad;
}