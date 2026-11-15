package vinicius.desafiont.biblioteca_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vinicius.desafiont.biblioteca_api.model.Livro;

public interface BookRepository extends JpaRepository<Livro, Long> {}



