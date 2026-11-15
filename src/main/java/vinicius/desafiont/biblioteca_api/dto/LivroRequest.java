package vinicius.desafiont.biblioteca_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LivroRequest(
        @NotBlank String titulo,
        @NotBlank String autor,
        @NotNull @Positive Integer anoPublicacao) {
}