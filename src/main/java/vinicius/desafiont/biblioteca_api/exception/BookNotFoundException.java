package vinicius.desafiont.biblioteca_api.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Book of id " + id + " not found");
    }
}
