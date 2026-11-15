package vinicius.desafiont.biblioteca_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vinicius.desafiont.biblioteca_api.dto.BookRequest;
import vinicius.desafiont.biblioteca_api.dto.BookResponse;
import vinicius.desafiont.biblioteca_api.service.BookService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody @Valid BookRequest request){
        BookResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public BookResponse searchForId(@PathVariable Long id){
        return service.findById(id);
    }

    @GetMapping
    public List<BookResponse> findAll(){
        return service.findAll();
    }

}
