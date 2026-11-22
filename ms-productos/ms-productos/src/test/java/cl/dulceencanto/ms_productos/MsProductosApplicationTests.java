package cl.dulceencanto.ms_productos;

import cl.dulceencanto.ms_productos.config.DataLoader;
import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.repository.CategoriaRepository;
import cl.dulceencanto.ms_productos.repository.ProductoRepository;
import cl.dulceencanto.ms_productos.util.ProductoImagenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MsProductosApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private DataLoader dataLoader;

	@Test
	void contextLoads() {
	}

	@Test
	void cargaCategoriasYProductosDeEjemplo() {
		assertThat(categoriaRepository.count()).isEqualTo(4);
		assertThat(productoRepository.count()).isEqualTo(12);
		assertThat(productoRepository.findAll().stream().map(Producto::getStock))
				.filteredOn(stock -> stock <= 5)
				.hasSizeGreaterThanOrEqualTo(2);

		dataLoader.run();
		assertThat(categoriaRepository.count()).isEqualTo(4);
		assertThat(productoRepository.count()).isEqualTo(12);
		assertThat(productoRepository.findAll())
				.extracting(Producto::getImagenUrl)
				.allSatisfy(imagenUrl ->
						assertThat(imagenUrl).startsWith("https://"));
	}

	@Test
	void normalizaUrlsExistentesConPrefijoIncorrecto() {
		Producto producto = productoRepository.findAll().stream()
				.filter(item -> item.getNombre().equals("Caja de bombones artesanales"))
				.findFirst()
				.orElseThrow();
		producto.setImagenUrl("/images/productos/bombones-artesanales.jpg");
		productoRepository.saveAndFlush(producto);

		dataLoader.run();

		Producto actualizado = productoRepository.findById(producto.getId()).orElseThrow();
		assertThat(actualizado.getImagenUrl())
				.isEqualTo("https://odenchocolates.cl/wp-content/uploads/2025/05/image_81e8ebf4.jpeg");
		assertThat(ProductoImagenUtils.normalize("/images/productos/http://example.com/a.jpg"))
				.isEqualTo("http://example.com/a.jpg");
		assertThat(ProductoImagenUtils.normalize("/images/productos/a.jpg"))
				.isEqualTo("/images/productos/a.jpg");

		assertThat(productoRepository.findById(producto.getId()).orElseThrow().getImagenUrl())
				.isEqualTo(actualizado.getImagenUrl());

		Producto trufas = productoRepository.findAll().stream()
				.filter(item -> item.getNombre().equals("Trufas de chocolate"))
				.findFirst()
				.orElseThrow();
		trufas.setImagenUrl("https://www.recetasnestle.com.pe/recetas/trufas-de-chocolate");
		productoRepository.saveAndFlush(trufas);
		dataLoader.run();
		assertThat(productoRepository.findById(trufas.getId()).orElseThrow().getImagenUrl())
				.isEqualTo("https://www.recetasnestle.com.pe/sites/default/files/styles/recipe_detail_desktop_new/public/srh_recipes/fa9696f5ff6ca9df9a980e828267192a.png?itok=WWBmQT8L");
	}

	@Test
	void openApiDocsAreGenerated() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"openapi\"")));
	}

}
