package cl.dulceencanto.ms_ventas.service;

import cl.dulceencanto.ms_ventas.dto.ItemVentaRequest;
import cl.dulceencanto.ms_ventas.dto.ProductoExternalDTO;
import cl.dulceencanto.ms_ventas.dto.VentaRequest;
import cl.dulceencanto.ms_ventas.model.Boleta;
import cl.dulceencanto.ms_ventas.model.DetalleBoleta;
import cl.dulceencanto.ms_ventas.repository.BoletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private BoletaRepository boletaRepository;

    @Value("${productos.service.url}")
    private String productosUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    public Boleta obtenerPorId(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boleta no encontrada"));
    }

    @Transactional
    public Boleta crearVenta(VentaRequest request) {
        if (request.getUsuarioId() == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar usuarioId y al menos un producto");
        }

        Boleta boleta = new Boleta();
        boleta.setUsuarioId(request.getUsuarioId());
        boleta.setFecha(LocalDateTime.now());
        boleta.setEstado("COMPLETADA");

        List<DetalleBoleta> detalles = new ArrayList<>();
        int totalGeneral = 0;

        for (ItemVentaRequest item : request.getItems()) {
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad inválida");
            }

            ProductoExternalDTO producto;
            try {
                // 1. Consultar precio y existencia real en ms-productos
                producto = restTemplate.getForObject(
                        productosUrl + "/api/v1/productos/" + item.getProductoId(),
                        ProductoExternalDTO.class
                );
            } catch (HttpClientErrorException.NotFound e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto ID " + item.getProductoId() + " no existe");
            } catch (ResourceAccessException e) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de productos no está disponible");
            }

            if (producto == null || producto.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Sin stock suficiente para producto ID " + item.getProductoId());
            }

            // 2. Descontar stock en ms-productos
            try {
                restTemplate.put(
                        productosUrl + "/api/v1/productos/" + item.getProductoId() + "/descontar-stock?cantidad=" + item.getCantidad(),
                        null
                );
            } catch (HttpClientErrorException.Conflict e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflicto de stock al procesar la venta");
            } catch (ResourceAccessException e) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de productos no está disponible");
            }

            // 3. Calcular subtotales en el servidor
            int subtotal = producto.getPrecio() * item.getCantidad();
            totalGeneral += subtotal;

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setBoleta(boleta);
            detalle.setProductoId(producto.getId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            detalles.add(detalle);
        }

        boleta.setTotal(totalGeneral);
        boleta.setDetalles(detalles);
        return boletaRepository.save(boleta);
    }

    @Transactional
    public Boleta anularVenta(Long id) {
        Boleta boleta = obtenerPorId(id);
        if ("ANULADA".equals(boleta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La boleta ya se encuentra anulada");
        }

        for (DetalleBoleta detalle : boleta.getDetalles()) {
            try {
                restTemplate.put(
                        productosUrl + "/api/v1/productos/" + detalle.getProductoId() + "/reponer-stock?cantidad=" + detalle.getCantidad(),
                        null
                );
            } catch (ResourceAccessException e) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo reponer stock: servicio de productos caído");
            }
        }

        boleta.setEstado("ANULADA");
        return boletaRepository.save(boleta);
    }
}