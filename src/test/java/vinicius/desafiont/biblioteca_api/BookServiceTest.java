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

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository repository;

	@InjectMocks
	private BookService service;


	@Test
	void shouldCreateRequestedBook() {

		var request = new BookRequest("Senhor dos Anéis", "JRR Tolkien", 1937);

		when(repository.save(any(Book.class))).thenAnswer(invoke -> {
			Book book = invoke.getArgument(0);
			ReflectionTestUtils.setField(book, "id", 1L);
			return book;
		});

		BookResponse response = service.create(request);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.title()).isEqualTo("Senhor dos Anéis");
		assertThat(response.author()).isEqualTo("JRR Tolkien");
		assertThat(response.publicationYear()).isEqualTo(1937);
		verify(repository).save(any(Book.class));
	}

	@Test
	void shouldRaiseExceptionWhenBookDoesNotExist(){
		when(repository.findById(9999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.searchForId(9999L))
				.isInstanceOf(BookNotFoundException.class)
				.hasMessageContaining("9999");
	}

}
