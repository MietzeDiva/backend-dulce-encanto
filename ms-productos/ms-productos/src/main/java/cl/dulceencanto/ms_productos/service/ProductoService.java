package cl.dulceencanto.ms_productos.service;

import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }
    
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
}