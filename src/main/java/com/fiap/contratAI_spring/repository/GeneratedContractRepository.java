package com.fiap.contratAI_spring.repository;

import com.fiap.contratAI_spring.model.GeneratedContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedContractRepository extends JpaRepository<GeneratedContract, Long> {
}
