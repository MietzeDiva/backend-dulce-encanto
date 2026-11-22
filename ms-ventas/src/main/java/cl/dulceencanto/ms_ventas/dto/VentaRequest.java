package cl.dulceencanto.ms_ventas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Ya NO trae usuarioId: el comprador se toma del JWT (claim userId), así nadie puede comprar a nombre de otro.
 * Si el front aún envía usuarioId, se ignora.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VentaRequest {
    @NotEmpty(message = "Debe indicar al menos un producto")
    @Valid
    private List<ItemVentaRequest> items;
}
