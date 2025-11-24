package com.fiap.contratAI_spring.repository;

import com.fiap.contratAI_spring.model.AlteredContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlteredContractRepository extends JpaRepository<AlteredContract, Long> {
}
