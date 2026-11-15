package vinicius.desafiont.biblioteca_api.service;

import vinicius.desafiont.biblioteca_api.dto.LivroRequest;
import vinicius.desafiont.biblioteca_api.dto.LivroResponse;
import vinicius.desafiont.biblioteca_api.exception.LivroNaoEncontradoException;
import vinicius.desafiont.biblioteca_api.model.Livro;
import vinicius.desafiont.biblioteca_api.repository.LivroRepository;

public class LivroService {

    private final LivroRepository repository;

    public LivroService (LivroRepository repository){
        this.repository = repository;
    }

    public LivroResponse create (LivroRequest request){
        Livro livro = new Livro(request.titulo(), request.autor(), request.anoPublicacao());
        return LivroResponse.de(repository.save(livro));
    }

    public LivroResponse searchForId(Long id){
        return LivroResponse.de(searchEntity(id));
    }

    private Livro searchEntity (Long id) {
        return repository.findById(id).orElseThrow(() -> new LivroNaoEncontradoException(id));
    }

}
