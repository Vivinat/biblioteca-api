package vinicius.desafiont.biblioteca_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vinicius.desafiont.biblioteca_api.model.Livro;
import vinicius.desafiont.biblioteca_api.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class BookIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository repository;

	@BeforeEach
	void cleanDatabase(){
		repository.deleteAll();
	}

	@Test
	void shouldCreateAndPersistInDatabase() throws Exception {
		mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"titulo": "Senhor dos Aneis", "autor": "JRR Tolkien", "anoPublicacao": 1954}
						"""))
				.andExpect(status().isCreated());

		assertThat(repository.count()).isEqualTo(1);
		assertThat(repository.findAll().get(0).getTitulo()).isEqualTo("Senhor dos Aneis");
	}

	@Test
	void shouldUpdateBookInDatabase() throws Exception {
		Livro saved = repository.save(new Livro("Fractais Monocromaticos", "Vinicius Mota", 2026));

		mockMvc.perform(put("/books/" + saved.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"titulo": "Senhor dos Aneis", "autor": "JRR Tolkien", "anoPublicacao": 1954}
						"""))
				.andExpect(status().isOk());

		Livro updated = repository.findById(saved.getId()).orElseThrow();
		assertThat(updated.getTitulo()).isEqualTo("Senhor dos Aneis");
		assertThat(updated.getAutor()).isEqualTo("JRR Tolkien");
		assertThat(updated.getAnoPublicacao()).isEqualTo(1954);
	}

	@Test
	void shouldDeleteBookFromDatabase() throws Exception {
		Livro saved = repository.save(new Livro("Fractais Monocromaticos", "Vinicius Mota", 2026));

		mockMvc.perform(delete("/books/" + saved.getId()))
				.andExpect(status().isNoContent());

		assertThat(repository.existsById(saved.getId())).isFalse();
	}

	@Test
	void shouldListAllBooks() throws Exception {

		repository.save(new Livro("Fractais Monocromaticos", "Vinicius Mota", 2026));
		repository.save(new Livro("Senhor dos Aneis", "JRR Tolkien", 1954));

		mockMvc.perform(get("/books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].titulo").value("Fractais Monocromaticos"))
				.andExpect(jsonPath("$[1].titulo").value("Senhor dos Aneis"));
	}

	@Test
	void shouldFindBookById() throws Exception {
		Livro saved = repository.save(new Livro("Fractais Monocromaticos", "Vinicius Mota", 2026));

		mockMvc.perform(get("/books/" + saved.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(saved.getId()))
				.andExpect(jsonPath("$.titulo").value("Fractais Monocromaticos"))
				.andExpect(jsonPath("$.autor").value("Vinicius Mota"))
				.andExpect(jsonPath("$.anoPublicacao").value(2026));
	}

	@Test
	void shouldReturnProblemDetailWhenBookNotFound() throws Exception {
		mockMvc.perform(get("/books/9999"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.title").value("Not Found"))
				.andExpect(jsonPath("$.detail").value("Book with id 9999 not found"))
				.andExpect(jsonPath("$.instance").value("/books/9999"));
	}

}