package com.vira.paas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vira.paas.dtos.LoginDto;
import com.vira.paas.dtos.TokenDto;
import com.vira.paas.dtos.UserDto;
import com.vira.paas.mappers.UserMapper;
import com.vira.paas.models.UserModel;
import com.vira.paas.services.JwtService;
import com.vira.paas.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto request) {
        return ResponseEntity.ok(mapper.toDto(userService.create(mapper.toModel(request))));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserModel user = userService.findByUsername(request.username());
        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(new TokenDto(token, user.getUsername(), user.getRole()));
    }
}
