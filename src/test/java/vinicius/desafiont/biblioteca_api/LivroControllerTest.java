package vinicius.desafiont.biblioteca_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vinicius.desafiont.biblioteca_api.controller.LivroController;
import vinicius.desafiont.biblioteca_api.dto.LivroResponse;
import vinicius.desafiont.biblioteca_api.exception.LivroNaoEncontradoException;
import vinicius.desafiont.biblioteca_api.service.LivroService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LivroService service;

	@Test
	void shouldReturn201WhenCreated() throws Exception {
		when(service.create(any()))
				.thenReturn(new LivroResponse(1L, "Senhor dos Aneis", "JRR Tolkien", 1937));

		mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"titulo": "Senhor dos Aneis", "autor": "JRR Tolkien", "anoPublicacao": 1937}
						"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", endsWith("/books/1")))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.titulo").value("Senhor dos Aneis"));
	}

	@Test
	void shouldReturn400WhenTitleEmpty() throws Exception{
		mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
                {"titulo": "", "autor": "Fulano", "anoPublicacao": 1937}
                """))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(service);
	}

	@Test
	void shouldReturn404WhenBookDoesNotExist() throws Exception {
		when(service.searchForId(9999L)).thenThrow(new LivroNaoEncontradoException(9999L));

		mockMvc.perform(get("/books/9999"))
				.andExpect(status().isNotFound());
	}

}
