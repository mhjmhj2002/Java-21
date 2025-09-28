package com.example.delivery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteInputDTO(
    @NotBlank(message = "O nome não pode ser vazio.")
    String nome,

    @NotBlank(message = "O e-mail não pode ser vazio.")
    @Email(message = "O formato do e-mail é inválido.")
    String email,

    String telefone
) {}
