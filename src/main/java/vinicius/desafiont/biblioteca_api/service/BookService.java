package vinicius.desafiont.biblioteca_api.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import vinicius.desafiont.biblioteca_api.dto.BookRequest;
import vinicius.desafiont.biblioteca_api.dto.BookResponse;
import vinicius.desafiont.biblioteca_api.exception.BookNotFoundException;
import vinicius.desafiont.biblioteca_api.model.Livro;
import vinicius.desafiont.biblioteca_api.repository.BookRepository;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository){
        this.repository = repository;
    }

    public BookResponse create (BookRequest request){
        Livro livro = new Livro(request.titulo(), request.autor(), request.anoPublicacao());
        return BookResponse.from(repository.save(livro));
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

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        Livro livro = searchEntityById(id);
        livro.updateBook(request.titulo(), request.autor(), request.anoPublicacao());
        return BookResponse.from(livro);
    }

    private Livro searchEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }


}
