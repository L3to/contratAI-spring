package com.fiap.contratAI_spring.controller;

import com.fiap.contratAI_spring.dto.ContractDTO;
import com.fiap.contratAI_spring.dto.GenerateContractRequest;
import com.fiap.contratAI_spring.model.Contract;
import com.fiap.contratAI_spring.model.GeneratedContract;
import com.fiap.contratAI_spring.repository.ContractRepository;
import com.fiap.contratAI_spring.repository.GeneratedContractRepository;
import com.fiap.contratAI_spring.service.ContractAIService;
import com.fiap.contratAI_spring.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
@Tag(name = "Contratos", description = "Endpoints para geração, análise e gerenciamento de contratos utilizando IA")
@SecurityRequirement(name = "basicAuth")
public class ContractController {

        private final ContractAIService contractAIService;
        private final ContractRepository contractRepository;
        private final GeneratedContractRepository generatedContractRepository;
        private final UserService userService;

        public ContractController(
                        ContractAIService contractAIService,
                        ContractRepository contractRepository,
                        GeneratedContractRepository generatedContractRepository,
                        UserService userService) {

                this.contractAIService = contractAIService;
                this.contractRepository = contractRepository;
                this.generatedContractRepository = generatedContractRepository;
                this.userService = userService;
        }

    @Operation(
            summary = "Gerar contrato usando IA",
            description = "Gera um contrato automático baseado nos termos fornecidos. " +
                    "Disponível para CLIENT, LAWYER e ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contrato gerado com sucesso",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(responseCode = "400", description = "Campo 'terms' inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
    })
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('CLIENT', 'LAWYER', 'ADMIN')")
        public ResponseEntity<String> generateContract(@RequestBody GenerateContractRequest request,
                                                                                                   @AuthenticationPrincipal UserDetails userDetails) {

        if (request.terms() == null || request.terms().isBlank()) {
            return ResponseEntity.badRequest().body("O campo 'terms' é obrigatório.");
        }

                String generated = contractAIService.generateContract(request.terms());

                try {
                        com.fiap.contratAI_spring.model.User owner = userService.getUserByUsername(userDetails.getUsername());
                        GeneratedContract gen = new GeneratedContract();
                        gen.setContent(generated);
                        String title = request.terms();
                        if (title != null && title.length() > 120) {
                                title = title.substring(0, 120);
                        }
                        gen.setTitle("Contrato gerado: " + (title == null ? "" : title));
                        gen.setOwner(owner);
                        gen.setStatus(com.fiap.contratAI_spring.model.enums.ContractStatus.ANALYZED);
                        gen.setSourceTerms(request.terms());

                        generatedContractRepository.save(gen);
                } catch (Exception e) {
                        System.err.println("Falha ao salvar contrato gerado: " + e.getMessage());
                }

                return ResponseEntity.ok(generated);
    }

    @Operation(
            summary = "Analisar contrato (AI)",
            description = "Envia contrato para análise via RabbitMQ. Apenas LAWYER e ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Enviado para análise", content = @Content),
            @ApiResponse(responseCode = "400", description = "Contrato vazio", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
    })
    @PostMapping("/analyze")
    @PreAuthorize("hasAnyRole('LAWYER', 'ADMIN')")
    public ResponseEntity<String> analyzeContract(
            @RequestBody String rawContract,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (rawContract == null || rawContract.isBlank()) {
            return ResponseEntity.badRequest().body("O contrato enviado está vazio.");
        }

        Long userId = userService.getUserIdByUsername(userDetails.getUsername());
        contractAIService.sendContractForAnalysis(rawContract, userId);

        return ResponseEntity.accepted()
                .body("Análise enviada. O resultado será disponibilizado posteriormente.");
    }

    @Operation(
            summary = "Listar contratos",
            description = "Lista todos os contratos paginados. Permitido para qualquer usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER', 'CLIENT')")
    public ResponseEntity<Page<ContractDTO>> getAllContracts(@ParameterObject Pageable pageable) {

        Page<ContractDTO> page = contractRepository.findAll(pageable)
                .map(c -> new ContractDTO(
                        c.getId(),
                        c.getTitle(),
                        c.getContent(),
                        c.getStatus().name(),
                        c.getCreatedAt(),
                        c.getOwner().getId(),
                        c.getOwner().getName(),
                        c.getOwner().getEmail()
                ));

        return ResponseEntity.ok(page);
    }
}
