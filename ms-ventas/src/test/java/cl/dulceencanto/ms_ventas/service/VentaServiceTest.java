package cl.dulceencanto.ms_ventas.service;

import cl.dulceencanto.ms_ventas.dto.ProductoExternalDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VentaServiceTest {

    @Test
    void usaPrecioOfertaCuandoEsMenorAlPrecioNormal() {
        ProductoExternalDTO producto = new ProductoExternalDTO();
        producto.setPrecio(12900);
        producto.setPrecioOferta(9900);

        assertThat(VentaService.precioVigente(producto)).isEqualTo(9900);
    }

    @Test
    void usaPrecioNormalCuandoLaOfertaNoEsValida() {
        ProductoExternalDTO producto = new ProductoExternalDTO();
        producto.setPrecio(12900);
        producto.setPrecioOferta(12900);

        assertThat(VentaService.precioVigente(producto)).isEqualTo(12900);
    }
}
