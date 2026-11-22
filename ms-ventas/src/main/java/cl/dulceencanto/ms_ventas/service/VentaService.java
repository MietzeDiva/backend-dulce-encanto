package cl.dulceencanto.ms_ventas.service;

import cl.dulceencanto.ms_ventas.dto.ItemVentaRequest;
import cl.dulceencanto.ms_ventas.dto.ProductoExternalDTO;
import cl.dulceencanto.ms_ventas.dto.VentaRequest;
import cl.dulceencanto.ms_ventas.model.Boleta;
import cl.dulceencanto.ms_ventas.model.DetalleBoleta;
import cl.dulceencanto.ms_ventas.repository.BoletaRepository;
import cl.dulceencanto.ms_ventas.security.ServiceTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    private final BoletaRepository boletaRepository;
    private final String productosUrl;
    private final RestClient restClient;

    public VentaService(BoletaRepository boletaRepository,
                        ServiceTokenProvider tokens,
                        @Value("${productos.service.url}") String productosUrl) {
        this.boletaRepository = boletaRepository;
        this.productosUrl = productosUrl;
        // Cada llamada a ms-productos lleva un token de servicio (rol SERVICIO), no el token del cliente
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.generar())
                .build();
    }

    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    public List<Boleta> listarPorUsuario(Long usuarioId) {
        return boletaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public Boleta obtenerPorId(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boleta no encontrada"));
    }

    /**
     * Regla de negocio: precio y stock se leen SIEMPRE de ms-productos (el front no manda precios).
     * Si un ítem falla, se devuelve el stock de los ítems ya descontados (compensación) y no se crea boleta.
     */
    @Transactional
    public Boleta crearVenta(Long usuarioId, VentaRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar al menos un producto");
        }

        Boleta boleta = new Boleta();
        boleta.setUsuarioId(usuarioId);
        boleta.setFecha(LocalDateTime.now());
        boleta.setEstado("COMPLETADA");

        List<DetalleBoleta> detalles = new ArrayList<>();
        List<ItemVentaRequest> descontados = new ArrayList<>();
        int totalGeneral = 0;

        try {
            for (ItemVentaRequest item : request.getItems()) {
                ProductoExternalDTO producto = obtenerProducto(item.getProductoId());

                if (producto == null || producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Sin stock suficiente para producto ID " + item.getProductoId());
                }

                descontarStock(item);
                descontados.add(item);

                int precioUnitario = precioVigente(producto);
                int subtotal = precioUnitario * item.getCantidad();
                totalGeneral += subtotal;

                DetalleBoleta detalle = new DetalleBoleta();
                detalle.setBoleta(boleta);
                detalle.setProductoId(producto.getId());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setSubtotal(subtotal);
                detalles.add(detalle);
            }

            boleta.setTotal(totalGeneral);
            boleta.setDetalles(detalles);
            return boletaRepository.save(boleta);
        } catch (RuntimeException e) {
            revertirStock(descontados);
            throw e;
        }
    }

    static int precioVigente(ProductoExternalDTO producto) {
        Integer oferta = producto.getPrecioOferta();
        return oferta != null && oferta < producto.getPrecio() ? oferta : producto.getPrecio();
    }

    @Transactional
    public Boleta anularVenta(Long id) {
        Boleta boleta = obtenerPorId(id);
        if ("ANULADA".equals(boleta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La boleta ya se encuentra anulada");
        }
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            try {
                restClient.put()
                        .uri(productosUrl + "/api/v1/productos/" + detalle.getProductoId()
                                + "/reponer-stock?cantidad=" + detalle.getCantidad())
                        .retrieve()
                        .toBodilessEntity();
            } catch (ResourceAccessException e) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "No se pudo reponer stock: servicio de productos caído");
            } catch (RestClientException e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ms-productos rechazó la reposición de stock");
            }
        }
        boleta.setEstado("ANULADA");
        return boletaRepository.save(boleta);
    }

    private ProductoExternalDTO obtenerProducto(Long productoId) {
        try {
            return restClient.get()
                    .uri(productosUrl + "/api/v1/productos/" + productoId)
                    .retrieve()
                    .body(ProductoExternalDTO.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto ID " + productoId + " no existe");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al consultar ms-productos");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de productos no está disponible");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al consultar ms-productos");
        }
    }

    private void descontarStock(ItemVentaRequest item) {
        try {
            restClient.put()
                    .uri(productosUrl + "/api/v1/productos/" + item.getProductoId()
                            + "/descontar-stock?cantidad=" + item.getCantidad())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflicto de stock al procesar la venta");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ms-productos rechazó el descuento de stock");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de productos no está disponible");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ms-productos rechazó el descuento de stock");
        }
    }

    private void revertirStock(List<ItemVentaRequest> descontados) {
        for (ItemVentaRequest item : descontados) {
            try {
                restClient.put()
                        .uri(productosUrl + "/api/v1/productos/" + item.getProductoId()
                                + "/reponer-stock?cantidad=" + item.getCantidad())
                        .retrieve()
                        .toBodilessEntity();
            } catch (RestClientException e) {
                log.error("No se pudo revertir el stock del producto {} (cantidad {}): {}",
                        item.getProductoId(), item.getCantidad(), e.getMessage());
            }
        }
    }
}
