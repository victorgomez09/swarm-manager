package com.vira.paas.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vira.paas.models.UserModel;
import com.vira.paas.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserModel findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException());
    }

    public UserModel create(UserModel model) {
        if (repository.existsByUsername(model.getUsername())) {
            return null;
        }

        UserModel user = UserModel.builder()
                .username(model.getUsername())
                .password(passwordEncoder.encode(model.getPassword()))
                .role(model.getRole() != null ? model.getRole() : "ROLE_DEVELOPER")
                .build();

        return repository.save(user);
    }
}
