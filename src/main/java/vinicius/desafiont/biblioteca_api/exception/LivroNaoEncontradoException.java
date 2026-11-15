package vinicius.desafiont.biblioteca_api.exception;

public class LivroNaoEncontradoException extends RuntimeException {
    public LivroNaoEncontradoException(Long id) {
        super("LIVRO DE ID " + id + " NAO ENCONTRADO");
    }
}
