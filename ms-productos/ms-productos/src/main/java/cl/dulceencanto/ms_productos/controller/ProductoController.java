package cl.dulceencanto.ms_productos.controller;

import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardarProducto(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/descontar-stock")
    public ResponseEntity<Producto> descontarStock(@PathVariable Long id, @RequestParam int cantidad) {
        return ResponseEntity.ok(productoService.descontarStock(id, cantidad));
    }

    @PutMapping("/{id}/reponer-stock")
    public ResponseEntity<Producto> reponerStock(@PathVariable Long id, @RequestParam int cantidad) {
        return ResponseEntity.ok(productoService.reponerStock(id, cantidad));
    }
}