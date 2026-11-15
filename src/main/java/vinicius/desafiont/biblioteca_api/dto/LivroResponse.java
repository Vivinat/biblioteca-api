package vinicius.desafiont.biblioteca_api.dto;

import vinicius.desafiont.biblioteca_api.model.Livro;

public record LivroResponse (Long id, String titulo, String autor, Integer anoPublicacao){
    public static LivroResponse de (Livro livro) {
        return new LivroResponse(livro.getId(), livro.getTitulo(), livro.getAutor(), livro.getAnoPublicacao());
    }
}
