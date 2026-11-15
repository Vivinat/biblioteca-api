package vinicius.desafiont.biblioteca_api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vinicius.desafiont.biblioteca_api.dto.BookRequest;
import vinicius.desafiont.biblioteca_api.dto.BookResponse;
import vinicius.desafiont.biblioteca_api.exception.BookNotFoundException;
import vinicius.desafiont.biblioteca_api.model.Livro;
import vinicius.desafiont.biblioteca_api.repository.BookRepository;
import vinicius.desafiont.biblioteca_api.service.BookService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository repository;

	@InjectMocks
	private BookService service;


	@Test
	void shouldCreateRequestedBook() {

		var request = new BookRequest("Senhor dos Aneis", "JRR Tolkien", 1954);

		when(repository.save(any(Livro.class))).thenAnswer(invoke -> {
			Livro livro = invoke.getArgument(0);
			ReflectionTestUtils.setField(livro, "id", 1L);
			return livro;
		});

		BookResponse response = service.create(request);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.titulo()).isEqualTo("Senhor dos Aneis");
		assertThat(response.autor()).isEqualTo("JRR Tolkien");
		assertThat(response.anoPublicacao()).isEqualTo(1954);
		verify(repository).save(any(Livro.class));
	}

	@Test
	void shouldRaiseExceptionWhenBookDoesNotExist(){
		when(repository.findById(9999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.findById(9999L))
				.isInstanceOf(BookNotFoundException.class)
				.hasMessageContaining("9999");
	}

	@Test
	void shouldReturnAllBooks(){
		var firstRequest = new Livro("Senhor dos Aneis", "JRR Tolkien", 1954);
		var secondRequest = new Livro("Fractais Monocromaticos", "Vinicius Mota", 2026);
		when(repository.findAll()).thenReturn(List.of(firstRequest,secondRequest));

		List<BookResponse> result = service.findAll();

		assertThat(result).hasSize(2);
		assertThat(result).extracting(BookResponse::titulo).containsExactly("Senhor dos Aneis","Fractais Monocromaticos");
	}

	@Test
	void shouldReturnEmptyListWhenNoBooks(){
		when(repository.findAll()).thenReturn(List.of());

		assertThat(service.findAll()).isEmpty();
	}

	@Test
	void shouldDeleteExistingBook(){
		var livro = new Livro("Senhor dos Aneis", "JRR Tolkien", 1954);
		when(repository.findById(1L)).thenReturn(Optional.of(livro));

		service.delete(1L);
		verify(repository).delete(livro);

	}

	@Test
	void shouldThrowExceptionWhenDeletingNonexistentBook(){
		when(repository.findById(9999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.delete(9999L))
				.isInstanceOf(BookNotFoundException.class)
				.hasMessageContaining("9999");

		verify(repository, never()).delete(any());
	}

	@Test
	void shouldUpdateExistingBook(){
		var livro = new Livro("Senhor dos Aneis", "JRR Tolkien", 1954);
		when(repository.findById(1L)).thenReturn(Optional.of(livro));
		var request = new BookRequest("Fractais Monocromaticos", "Vinicius Mota", 2026);

		BookResponse response = service.update(1L, request);

		assertThat(response.titulo()).isEqualTo("Fractais Monocromaticos");
		assertThat(response.autor()).isEqualTo("Vinicius Mota");
		assertThat(response.anoPublicacao()).isEqualTo(2026);

		assertThat(livro.getTitulo()).isEqualTo("Fractais Monocromaticos");

		verify(repository, never()).save(any());
	}

	@Test
	void shouldThrowExceptionWhenUpdatingNonexistentBook(){
		when(repository.findById(9999L)).thenReturn(Optional.empty());
		var request = new BookRequest("Fractais Monocromaticos", "Vinicius Mota", 2026);

		assertThatThrownBy(() -> service.update(9999L, request))
				.isInstanceOf(BookNotFoundException.class)
				.hasMessageContaining("9999");
	}

}
