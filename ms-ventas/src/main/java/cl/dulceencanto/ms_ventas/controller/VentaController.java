package cl.dulceencanto.ms_ventas.controller;

import cl.dulceencanto.ms_ventas.dto.VentaRequest;
import cl.dulceencanto.ms_ventas.model.Boleta;
import cl.dulceencanto.ms_ventas.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Compras y boletas (requiere Bearer token)")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Operation(summary = "Listar todas las boletas (ADMIN o VENDEDOR)")
    @GetMapping
    public ResponseEntity<List<Boleta>> listar() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @Operation(summary = "Mis compras", description = "Boletas del usuario autenticado, de la más nueva a la más antigua.")
    @GetMapping("/mias")
    public ResponseEntity<List<Boleta>> mias(Authentication auth) {
        return ResponseEntity.ok(ventaService.listarPorUsuario(userId(auth)));
    }

    @Operation(summary = "Obtener boleta por id", description = "Un cliente solo puede ver sus propias boletas (403 si no).")
    @GetMapping("/{id}")
    public ResponseEntity<Boleta> obtenerPorId(@PathVariable Long id, Authentication auth) {
        Boleta boleta = ventaService.obtenerPorId(id);
        if (!esPersonal(auth) && !boleta.getUsuarioId().equals(userId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver esta boleta");
        }
        return ResponseEntity.ok(boleta);
    }

    @Operation(summary = "Crear venta",
            description = "El comprador sale del token. Precios y stock se validan en ms-productos; "
                    + "si falla algún ítem se revierte el stock ya descontado.")
    @PostMapping
    public ResponseEntity<Boleta> crear(@Valid @RequestBody VentaRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.crearVenta(userId(auth), request));
    }

    @Operation(summary = "Anular boleta (ADMIN)", description = "Repone el stock. 409 si ya estaba anulada.")
    @PutMapping("/{id}/anular")
    public ResponseEntity<Boleta> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anularVenta(id));
    }

    private Long userId(Authentication auth) {
        if (auth != null && auth.getDetails() instanceof Long id) return id;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sin identificador de usuario");
    }

    private boolean esPersonal(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VENDEDOR"));
    }
}
