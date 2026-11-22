package cl.dulceencanto.ms_productos.controller;

import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Lectura pública; escritura solo ADMIN; stock solo servicios internos o ADMIN")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Listar productos")
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @Operation(summary = "Obtener producto por id")
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear producto (ADMIN)", description = "Requiere categoria.id existente.")
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        producto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardarProducto(producto));
    }

    @Operation(summary = "Actualizar producto (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
    }

    @Operation(summary = "Eliminar producto (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Descontar stock (uso interno de ms-ventas)",
            description = "Solo rol SERVICIO (token interno de ms-ventas) o ADMIN. 409 si no hay stock suficiente.")
    @PutMapping("/{id}/descontar-stock")
    public ResponseEntity<Producto> descontarStock(@PathVariable Long id, @RequestParam int cantidad) {
        return ResponseEntity.ok(productoService.descontarStock(id, cantidad));
    }

    @Operation(summary = "Reponer stock (uso interno de ms-ventas)",
            description = "Solo rol SERVICIO (token interno de ms-ventas) o ADMIN.")
    @PutMapping("/{id}/reponer-stock")
    public ResponseEntity<Producto> reponerStock(@PathVariable Long id, @RequestParam int cantidad) {
        return ResponseEntity.ok(productoService.reponerStock(id, cantidad));
    }
}
