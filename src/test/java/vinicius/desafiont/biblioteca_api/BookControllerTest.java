package vinicius.desafiont.biblioteca_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vinicius.desafiont.biblioteca_api.controller.BookController;
import vinicius.desafiont.biblioteca_api.dto.BookResponse;
import vinicius.desafiont.biblioteca_api.exception.BookNotFoundException;
import vinicius.desafiont.biblioteca_api.service.BookService;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookService service;

	@Test
	void shouldReturn201WhenCreated() throws Exception {
		when(service.create(any()))
				.thenReturn(new BookResponse(1L, "Senhor dos Aneis", "JRR Tolkien", 1954));

		mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"title": "Senhor dos Aneis", "author": "JRR Tolkien", "publicationYear": 1954}
						"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", endsWith("/books/1")))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Senhor dos Aneis"));
	}

	@Test
	void shouldReturn400WhenTitleEmpty() throws Exception{
		mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
                {"title": "", "author": "Fulano", "publicationYear": 1954}
                """))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(service);
	}

	@Test
	void shouldReturn404WhenBookDoesNotExist() throws Exception {
		when(service.searchForId(9999L)).thenThrow(new BookNotFoundException(9999L));

		mockMvc.perform(get("/books/9999"))
				.andExpect(status().isNotFound());
	}

}
