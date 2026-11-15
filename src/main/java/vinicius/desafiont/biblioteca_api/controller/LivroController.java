package vinicius.desafiont.biblioteca_api.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vinicius.desafiont.biblioteca_api.dto.LivroRequest;
import vinicius.desafiont.biblioteca_api.dto.LivroResponse;
import vinicius.desafiont.biblioteca_api.service.LivroService;

import java.net.URI;

@RestController
@RequestMapping("/books")
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LivroResponse> create(@RequestBody @Valid LivroRequest request){
        LivroResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public LivroResponse searchForId(@PathVariable Long id){
        return service.searchForId(id);
    }

}
