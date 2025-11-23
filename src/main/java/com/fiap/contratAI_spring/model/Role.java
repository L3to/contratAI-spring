package com.fiap.contratAI_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Ajuste para o Oracle
    private Long id;

    // Enum para garantir que os nomes dos papéis sejam consistentes
    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private RoleName name;

    @Override
    public String getAuthority() {
        // O Spring Security espera o nome do papel no formato "ROLE_NOME"
        return "ROLE_" + name.name();
    }
}