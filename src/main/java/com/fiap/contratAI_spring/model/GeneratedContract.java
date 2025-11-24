package com.fiap.contratAI_spring.model;

import com.fiap.contratAI_spring.model.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "generated_contracts")
@Getter
@Setter
@NoArgsConstructor
public class GeneratedContract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generated_contract_seq_gen")
    @SequenceGenerator(name = "generated_contract_seq_gen", sequenceName = "GENERATED_CONTRACT_SEQ", allocationSize = 1)
    private Long id;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    private String title;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private String sourceTerms;
}
