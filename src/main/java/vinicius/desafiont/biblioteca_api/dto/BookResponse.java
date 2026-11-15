package vinicius.desafiont.biblioteca_api.dto;

import vinicius.desafiont.biblioteca_api.model.Book;

public record BookResponse(Long id, String title, String author, Integer publicationYear){
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPublicationYear());
    }
}
