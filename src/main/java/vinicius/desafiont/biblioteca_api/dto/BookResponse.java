package vinicius.desafiont.biblioteca_api.dto;

import vinicius.desafiont.biblioteca_api.model.Livro;

public record BookResponse(Long id, String titulo, String autor, Integer anoPublicacao){
    public static BookResponse from(Livro livro) {
        return new BookResponse(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getAnoPublicacao());
    }
}
