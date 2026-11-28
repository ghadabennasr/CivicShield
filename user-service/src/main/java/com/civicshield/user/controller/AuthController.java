
package com.civicshield.user.controller;

import com.civicshield.user.dto.AuthResponse;
import com.civicshield.user.dto.LoginRequest;
import com.civicshield.user.dto.RegisterRequest;
import com.civicshield.user.dto.UserResponse;
import com.civicshield.user.entity.User;
import com.civicshield.user.service.AuthService;
import com.civicshield.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(
            UserService userService,
            AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.register(request);

        UserResponse response = userService.toUserResponse(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.authenticate(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(response);
    }
}
