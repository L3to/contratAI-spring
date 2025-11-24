package com.fiap.contratAI_spring.model;

import com.fiap.contratAI_spring.model.enums.ContractStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "altered_contracts")
@Getter
@Setter
@NoArgsConstructor
public class AlteredContract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "altered_contract_seq_gen")
    @SequenceGenerator(name = "altered_contract_seq_gen", sequenceName = "ALTERED_CONTRACT_SEQ", allocationSize = 1)
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

    private Long originalContractId;
}
