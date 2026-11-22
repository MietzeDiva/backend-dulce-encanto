package cl.dulceencanto.ms_productos.service;

import cl.dulceencanto.ms_productos.model.Categoria;
import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.repository.CategoriaRepository;
import cl.dulceencanto.ms_productos.repository.ProductoRepository;
import cl.dulceencanto.ms_productos.util.ProductoImagenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Producto guardarProducto(Producto producto) {
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe especificar categoria.id");
        }
        Categoria cat = categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        producto.setCategoria(cat);
        producto.setImagenUrl(ProductoImagenUtils.normalize(producto.getImagenUrl()));
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto datos) {
        Producto existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setImagenUrl(ProductoImagenUtils.normalize(datos.getImagenUrl()));
        if (datos.getCategoria() != null && datos.getCategoria().getId() != null) {
            Categoria cat = categoriaRepository.findById(datos.getCategoria().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
            existente.setCategoria(cat);
        }
        return productoRepository.save(existente);
    }

    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    @Transactional
    public Producto descontarStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }
        Producto prod = obtenerPorId(id);
        if (prod.getStock() < cantidad) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Stock insuficiente para " + prod.getNombre() + ". Disponible: " + prod.getStock());
        }
        prod.setStock(prod.getStock() - cantidad);
        return productoRepository.save(prod);
    }

    @Transactional
    public Producto reponerStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }
        Producto prod = obtenerPorId(id);
        prod.setStock(prod.getStock() + cantidad);
        return productoRepository.save(prod);
    }
}