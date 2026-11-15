package vinicius.desafiont.biblioteca_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vinicius.desafiont.biblioteca_api.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {}



