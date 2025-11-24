package com.fiap.contratAI_spring.controller;

import com.fiap.contratAI_spring.dto.UserRegistrationRequest;
import com.fiap.contratAI_spring.model.User;
import com.fiap.contratAI_spring.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários e autenticação")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma nova conta de usuário no sistema. O usuário pode ter um dos seguintes roles: ADMIN, LAWYER ou CLIENT. " +
                    "Se nenhum role for especificado, o padrão CLIENT será atribuído. " +
                    "A senha será automaticamente criptografada usando BCrypt. " +
                    "Este é um endpoint público que não requer autenticação."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário registrado com sucesso",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "Usuário registrado com sucesso. ID: 123")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos fornecidos (validação falhou) ou e-mail já existe",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = {
                                    @ExampleObject(name = "Email existente", value = "Email já está em uso."),
                                    @ExampleObject(name = "Validação", value = "O email deve ser um endereço de email válido.")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor durante o registro",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do usuário a ser registrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserRegistrationRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Cliente",
                                            summary = "Registro de um cliente",
                                            value = "{\"name\": \"João Silva\", \"email\": \"joao.silva@example.com\", \"password\": \"senha123\", \"role\": \"CLIENT\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Advogado",
                                            summary = "Registro de um advogado",
                                            value = "{\"name\": \"Maria Santos\", \"email\": \"maria.santos@example.com\", \"password\": \"senha123\", \"role\": \"LAWYER\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Administrador",
                                            summary = "Registro de um administrador",
                                            value = "{\"name\": \"Admin User\", \"email\": \"admin@example.com\", \"password\": \"senha123\", \"role\": \"ADMIN\"}"
                                    )
                            }
                    )
            )
            @Valid @RequestBody UserRegistrationRequest registrationRequest) {
        try {
            User registeredUser = authService.registerNewUser(registrationRequest);
            return new ResponseEntity<>("Usuário registrado com sucesso. ID: " + registeredUser.getId(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
