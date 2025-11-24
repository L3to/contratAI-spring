package com.fiap.contratAI_spring.dto;

import com.fiap.contratAI_spring.model.User;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
    }
}