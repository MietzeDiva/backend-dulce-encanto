package cl.dulceencanto.ms_productos.controller;

import cl.dulceencanto.ms_productos.model.Categoria;
import cl.dulceencanto.ms_productos.repository.CategoriaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Lectura pública; crear/editar/eliminar solo ADMIN")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Operation(summary = "Listar categorías")
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    @Operation(summary = "Obtener categoría por id")
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(buscar(id));
    }

    @Operation(summary = "Crear categoría (ADMIN)")
    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria categoria) {
        categoria.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaRepository.save(categoria));
    }

    @Operation(summary = "Actualizar categoría (ADMIN)")
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @Valid @RequestBody Categoria datos) {
        Categoria cat = buscar(id);
        cat.setNombre(datos.getNombre());
        cat.setDescripcion(datos.getDescripcion());
        return ResponseEntity.ok(categoriaRepository.save(cat));
    }

    @Operation(summary = "Eliminar categoría (ADMIN)",
            description = "Responde 409 si la categoría todavía tiene productos asociados.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaRepository.delete(buscar(id));
        categoriaRepository.flush(); // fuerza la validación de FK aquí para poder responder 409
        return ResponseEntity.noContent().build();
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }
}
