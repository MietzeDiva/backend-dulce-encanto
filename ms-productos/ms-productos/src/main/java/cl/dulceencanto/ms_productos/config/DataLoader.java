package cl.dulceencanto.ms_productos.config;

import cl.dulceencanto.ms_productos.model.Categoria;
import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.repository.CategoriaRepository;
import cl.dulceencanto.ms_productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public DataLoader(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.findAll().isEmpty()) {
            Categoria chocolates = guardarCategoria("Chocolates", "Chocolatería y dulces artesanales");
            Categoria tortas = guardarCategoria("Tortas y Pasteles", "Tortas y pasteles para compartir");
            Categoria panaderia = guardarCategoria("Panadería Artesanal", "Panes y bollería de elaboración artesanal");
            Categoria galletas = guardarCategoria("Galletas y Alfajores", "Galletas y alfajores hechos en casa");

            productoRepository.saveAll(List.of(
                    crearProducto("Caja de bombones artesanales", "Caja de 12 bombones variados de elaboración artesanal", 12900, 24, chocolates, "bombones-artesanales.jpg"),
                    crearProducto("Brownie de chocolate y nuez", "Brownie húmedo con chocolate bitter y nueces", 3900, 16, chocolates, "brownie-chocolate-nuez.jpg"),
                    crearProducto("Trufas de chocolate", "Caja de 6 trufas de chocolate bitter", 5500, 4, chocolates, "trufas-chocolate.jpg"),
                    crearProducto("Torta tres leches", "Torta mediana remojada en tres leches y cubierta con merengue", 24900, 3, tortas, "torta-tres-leches.jpg"),
                    crearProducto("Cheesecake de frutos rojos", "Cheesecake cremoso con salsa de frutos rojos", 21900, 9, tortas, "cheesecake-frutos-rojos.jpg"),
                    crearProducto("Pie de limón", "Base de masa dulce con crema de limón y merengue", 14900, 12, tortas, "pie-de-limon.jpg"),
                    crearProducto("Pan de masa madre", "Hogaza artesanal de fermentación lenta", 4500, 15, panaderia, "pan-masa-madre.jpg"),
                    crearProducto("Croissant de mantequilla", "Croissant hojaldrado elaborado con mantequilla", 2200, 30, panaderia, "croissant-mantequilla.jpg"),
                    crearProducto("Brioche artesanal", "Pan brioche suave, preparado con mantequilla y huevo", 3900, 11, panaderia, "brioche-artesanal.jpg"),
                    crearProducto("Alfajores de maicena", "Caja de 6 alfajores rellenos de manjar y cubiertos con coco", 6800, 5, galletas, "alfajores-maicena.jpg"),
                    crearProducto("Galletas de avena y chips", "Paquete de 8 galletas de avena con chips de chocolate", 4500, 18, galletas, "galletas-avena-chips.jpg"),
                    crearProducto("Galletas de mantequilla", "Paquete de 10 galletas de mantequilla", 3500, 20, galletas, "galletas-mantequilla.jpg")
            ));
        }
    }

    private Categoria guardarCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        return categoriaRepository.save(categoria);
    }

    private Producto crearProducto(String nombre, String descripcion, int precio, int stock,
                                   Categoria categoria, String imagenUrl) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        producto.setImagenUrl("/images/productos/" + imagenUrl);
        return producto;
    }
}
