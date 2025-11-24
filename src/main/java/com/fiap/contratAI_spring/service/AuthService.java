package com.fiap.contratAI_spring.service;

import com.fiap.contratAI_spring.dto.UserRegistrationRequest;
import com.fiap.contratAI_spring.model.Role;
import com.fiap.contratAI_spring.model.User;
import com.fiap.contratAI_spring.model.enums.RoleName;
import com.fiap.contratAI_spring.repository.RoleRepository;
import com.fiap.contratAI_spring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(UserRegistrationRequest registrationRequest) {

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso.");
        }

        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setName(registrationRequest.getName());

        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        Role userRole;
        String requestedRole = registrationRequest.getRole() != null ? registrationRequest.getRole().toUpperCase() : "CLIENT";

        try {
            RoleName roleName = RoleName.valueOf(requestedRole);
            Optional<Role> roleOptional = roleRepository.findByName(roleName);

            if (roleOptional.isEmpty()) {
                throw new RuntimeException("A Role '" + roleName + "' não foi encontrada no sistema. Contate o Administrador.");
            }
            userRole = roleOptional.get();

        } catch (IllegalArgumentException e) {
            userRole = roleRepository.findByName(RoleName.CLIENT)
                    .orElseThrow(() -> new RuntimeException("Role padrão 'CLIENT' não definida."));
        }

        user.setRoles(Collections.singleton(userRole));

        return userRepository.save(user);
    }
}