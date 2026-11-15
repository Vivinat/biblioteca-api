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
import vinicius.desafiont.biblioteca_api.model.Book;
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

		when(repository.save(any(Book.class))).thenAnswer(invoke -> {
			Book book = invoke.getArgument(0);
			ReflectionTestUtils.setField(book, "id", 1L);
			return book;
		});

		BookResponse response = service.create(request);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.title()).isEqualTo("Senhor dos Aneis");
		assertThat(response.author()).isEqualTo("JRR Tolkien");
		assertThat(response.publicationYear()).isEqualTo(1954);
		verify(repository).save(any(Book.class));
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
		var firstRequest = new Book("Senhor dos Aneis", "JRR Tolkien", 1954);
		var secondRequest = new Book("Fractais Monocromaticos", "Vinicius Mota", 2026);
		when(repository.findAll()).thenReturn(List.of(firstRequest,secondRequest));

		List<BookResponse> result = service.findAll();

		assertThat(result).hasSize(2);
		assertThat(result).extracting(BookResponse::title).containsExactly("Senhor dos Aneis","Fractais Monocromaticos");
	}

	@Test
	void shouldReturnEmptyListWhenNoBooks(){
		when(repository.findAll()).thenReturn(List.of());

		assertThat(service.findAll()).isEmpty();
	}

	@Test
	void shouldDeleteExistingBook(){
		var book = new Book("Senhor dos Aneis", "JRR Tolkien", 1954);
		when(repository.findById(1L)).thenReturn(Optional.of(book));

		service.delete(1L);
		verify(repository).delete(book);

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
		var book = new Book("Senhor dos Aneis", "JRR Tolkien", 1954);
		when(repository.findById(1L)).thenReturn(Optional.of(book));
		var request = new BookRequest("Fractais Monocromaticos", "Vinicius Mota", 2026);

		BookResponse response = service.update(1L, request);

		assertThat(response.title()).isEqualTo("Fractais Monocromaticos");
		assertThat(response.author()).isEqualTo("Vinicius Mota");
		assertThat(response.publicationYear()).isEqualTo(2026);

		assertThat(book.getTitle()).isEqualTo("Fractais Monocromaticos");

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
