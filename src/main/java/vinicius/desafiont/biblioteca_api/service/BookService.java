package vinicius.desafiont.biblioteca_api.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vinicius.desafiont.biblioteca_api.dto.BookRequest;
import vinicius.desafiont.biblioteca_api.dto.BookResponse;
import vinicius.desafiont.biblioteca_api.exception.BookNotFoundException;
import vinicius.desafiont.biblioteca_api.model.Book;
import vinicius.desafiont.biblioteca_api.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository){
        this.repository = repository;
    }

    public BookResponse create (BookRequest request){
        Book book = new Book(request.title(), request.author(), request.publicationYear());
        return BookResponse.from(repository.save(book));
    }

    public BookResponse findById(Long id){
        return BookResponse.from(searchEntityById(id));
    }

    public List<BookResponse> findAll() {
        return repository.findAll().stream().map(BookResponse::from).toList();
    }

    public void delete(Long id){
        repository.delete(searchEntityById(id));
    }

    private Book searchEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Book book = searchEntityById(id);
        book.updateBook(request.title(), request.author(), request.publicationYear());
        return BookResponse.from(book);
    }
}
