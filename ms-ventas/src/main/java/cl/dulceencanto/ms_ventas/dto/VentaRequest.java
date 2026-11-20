package cl.dulceencanto.ms_ventas.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaRequest {
    private Long usuarioId;
    private List<ItemVentaRequest> items;
}