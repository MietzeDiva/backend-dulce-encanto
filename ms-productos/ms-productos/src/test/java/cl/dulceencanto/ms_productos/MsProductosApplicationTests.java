package cl.dulceencanto.ms_productos;

import cl.dulceencanto.ms_productos.config.DataLoader;
import cl.dulceencanto.ms_productos.model.Producto;
import cl.dulceencanto.ms_productos.repository.CategoriaRepository;
import cl.dulceencanto.ms_productos.repository.ProductoRepository;
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
	}

	@Test
	void openApiDocsAreGenerated() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"openapi\"")));
	}

}
