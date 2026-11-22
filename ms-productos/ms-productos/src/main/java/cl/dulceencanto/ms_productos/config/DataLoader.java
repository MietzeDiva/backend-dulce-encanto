package cl.dulceencanto.ms_productos.config;

import cl.dulceencanto.ms_productos.model.Categoria;
import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.repository.CategoriaRepository;
import cl.dulceencanto.ms_productos.repository.ProductoRepository;
import cl.dulceencanto.ms_productos.util.ProductoImagenUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Map<String, String> IMAGENES_PRODUCTOS = Map.ofEntries(
            Map.entry("Caja de bombones artesanales", "https://odenchocolates.cl/wp-content/uploads/2025/05/image_81e8ebf4.jpeg"),
            Map.entry("Brownie de chocolate y nuez", "https://laroussecocina.mx/wp-content/uploads/2021/07/Brownies-con-nueces.png"),
            Map.entry("Trufas de chocolate", "https://www.recetasnestle.com.pe/sites/default/files/styles/recipe_detail_desktop_new/public/srh_recipes/fa9696f5ff6ca9df9a980e828267192a.png?itok=WWBmQT8L"),
            Map.entry("Torta tres leches", "https://www.pasteleriaelparron.cl/wp-content/uploads/2023/08/Tres-Leches-2024.png"),
            Map.entry("Cheesecake de frutos rojos", "https://d1uz88p17r663j.cloudfront.net/original/84d898a1d5ad99e2c66b1ff3659768db_Cheesecake_de_Frutos_del_Bosque.jpg"),
            Map.entry("Pie de limón", "https://www.recetasnestle.com.pe/sites/default/files/styles/recipe_detail_desktop_new/public/srh_recipes/a84dbcb89ae7273cc4508772f3b5ba1a.jpg?itok=Su4E2R5Y"),
            Map.entry("Pan de masa madre", "https://api.reciperm.io/services/recipe/p/asset/legacy/10399.webp"),
            Map.entry("Croissant de mantequilla", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS9UBNAWcnnashS2CA17x8YS8BCXnJd66vCsSGZwEiX7SrGG6xoHsqtFzs&s=10"),
            Map.entry("Brioche artesanal", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT01fs1htkx6dSgslo81tbwgOUA_EOakYeP-t03VbEWzJ8rZaRx3V1sTdSo&s=10"),
            Map.entry("Alfajores de maicena", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSMp0ldPXTbE_NounzWyqXVrjFkx6g98GvOchHOt1cRu5lZiLNeZbJdKLQ&s=10"),
            Map.entry("Galletas de avena y chips", "https://www.recetasnestle.com.pe/sites/default/files/styles/recipe_detail_desktop_new/public/srh_recipes/248a00f4aee379686e58755755a9d79d.jpg?itok=MhV5n6Wk"),
            Map.entry("Galletas de mantequilla", "https://recetasdecocina.elmundo.es/wp-content/uploads/2017/09/receta-galletas-mantequilla.jpg")
    );

    private static final Map<String, String> IMAGENES_ANTERIORES_NO_DIRECTAS = Map.of(
            "Trufas de chocolate", "https://www.recetasnestle.com.pe/recetas/trufas-de-chocolate",
            "Pie de limón", "https://www.goodnes.com/sites/g/files/jgfbjl321/files/srh_recipes/2789a84fb196e18212da63faf1995f84.jpg",
            "Galletas de avena y chips", "https://www.dosanclas.com.ar/wp-content/smush-webp/2021/08/galleta_avena_635x360px.jpg.webp"
    );

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
                    crearProducto("Caja de bombones artesanales", "Caja de 12 bombones variados de elaboración artesanal", 12900, 24, chocolates),
                    crearProducto("Brownie de chocolate y nuez", "Brownie húmedo con chocolate bitter y nueces", 3900, 16, chocolates),
                    crearProducto("Trufas de chocolate", "Caja de 6 trufas de chocolate bitter", 5500, 4, chocolates),
                    crearProducto("Torta tres leches", "Torta mediana remojada en tres leches y cubierta con merengue", 24900, 3, tortas),
                    crearProducto("Cheesecake de frutos rojos", "Cheesecake cremoso con salsa de frutos rojos", 21900, 9, tortas),
                    crearProducto("Pie de limón", "Base de masa dulce con crema de limón y merengue", 14900, 12, tortas),
                    crearProducto("Pan de masa madre", "Hogaza artesanal de fermentación lenta", 4500, 15, panaderia),
                    crearProducto("Croissant de mantequilla", "Croissant hojaldrado elaborado con mantequilla", 2200, 30, panaderia),
                    crearProducto("Brioche artesanal", "Pan brioche suave, preparado con mantequilla y huevo", 3900, 11, panaderia),
                    crearProducto("Alfajores de maicena", "Caja de 6 alfajores rellenos de manjar y cubiertos con coco", 6800, 5, galletas),
                    crearProducto("Galletas de avena y chips", "Paquete de 8 galletas de avena con chips de chocolate", 4500, 18, galletas),
                    crearProducto("Galletas de mantequilla", "Paquete de 10 galletas de mantequilla", 3500, 20, galletas)
            ));
        }

        productoRepository.findAll().forEach(producto -> {
            String imagenActual = ProductoImagenUtils.normalize(producto.getImagenUrl());
            String nombre = producto.getNombre();
            String imagenDeEjemplo = IMAGENES_PRODUCTOS.get(nombre);
            if (imagenDeEjemplo != null
                    && (imagenActual == null
                    || imagenActual.startsWith("/images/productos/")
                    || Objects.equals(imagenActual, IMAGENES_ANTERIORES_NO_DIRECTAS.get(nombre)))) {
                imagenActual = imagenDeEjemplo;
            }
            if (!Objects.equals(producto.getImagenUrl(), imagenActual)) {
                producto.setImagenUrl(imagenActual);
                productoRepository.save(producto);
            }
        });
    }

    private Categoria guardarCategoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        return categoriaRepository.save(categoria);
    }

    private Producto crearProducto(String nombre, String descripcion, int precio, int stock,
                                   Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        producto.setImagenUrl(IMAGENES_PRODUCTOS.get(nombre));
        return producto;
    }
}
