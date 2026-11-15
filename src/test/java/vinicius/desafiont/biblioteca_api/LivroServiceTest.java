package vinicius.desafiont.biblioteca_api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import vinicius.desafiont.biblioteca_api.dto.LivroRequest;
import vinicius.desafiont.biblioteca_api.dto.LivroResponse;
import vinicius.desafiont.biblioteca_api.exception.LivroNaoEncontradoException;
import vinicius.desafiont.biblioteca_api.model.Livro;
import vinicius.desafiont.biblioteca_api.repository.LivroRepository;
import vinicius.desafiont.biblioteca_api.service.LivroService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

	@Mock
	private LivroRepository repository;

	@InjectMocks
	private LivroService service;


	@Test
	void shouldCreateRequestedBook() {

		var request = new LivroRequest("Senhor dos Anéis", "JRR Tolkien", 1937);

		when(repository.save(any(Livro.class))).thenAnswer(invoke -> {
			Livro livro = invoke.getArgument(0);
			ReflectionTestUtils.setField(livro, "id", 1L);
			return livro;
		});

		LivroResponse response = service.create(request);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.titulo()).isEqualTo("Senhor dos Anéis");
		assertThat(response.autor()).isEqualTo("JRR Tolkien");
		assertThat(response.anoPublicacao()).isEqualTo(1937);
		verify(repository).save(any(Livro.class));
	}

	@Test
	void shouldRaiseExceptionWhenBookDoesNotExist(){
		when(repository.findById(9999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.searchForId(9999L))
				.isInstanceOf(LivroNaoEncontradoException.class)
				.hasMessageContaining("9999L");
	}

}
