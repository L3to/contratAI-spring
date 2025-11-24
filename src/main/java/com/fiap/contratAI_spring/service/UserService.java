package com.fiap.contratAI_spring.service;

import com.fiap.contratAI_spring.model.User;
import com.fiap.contratAI_spring.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getUserIdByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados: " + email));
        return user.getId();
    }

    public User getUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados: " + email));
    }
}
