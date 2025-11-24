package com.fiap.contratAI_spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser um endereço de email válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
    private String password;

    private String role;
}