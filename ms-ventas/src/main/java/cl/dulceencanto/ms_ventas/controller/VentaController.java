package cl.dulceencanto.ms_ventas.controller;

import cl.dulceencanto.ms_ventas.dto.VentaRequest;
import cl.dulceencanto.ms_ventas.model.Boleta;
import cl.dulceencanto.ms_ventas.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Boleta>> listar() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boleta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Boleta> crear(@RequestBody VentaRequest request) {
        return ResponseEntity.ok(ventaService.crearVenta(request));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<Boleta> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anularVenta(id));
    }
}