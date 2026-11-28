package com.civicshield.user.controller;

import com.civicshield.user.dto.RoleUpdateRequest;
import com.civicshield.user.dto.UserResponse;
import com.civicshield.user.entity.User;
import com.civicshield.user.service.RoleManagementService;
import com.civicshield.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final RoleManagementService roleManagementService;

    public UserController(
            UserService userService,
            RoleManagementService roleManagementService) {
        this.userService = userService;
        this.roleManagementService = roleManagementService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(userService.toUserResponse(user));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable UUID userId,
            @Valid @RequestBody RoleUpdateRequest request,
            @AuthenticationPrincipal User actor) {

        UserResponse response = roleManagementService.changeRole(
                actor.getId(),
                userId,
                request.role()
        );

        return ResponseEntity.ok(response);
    }
}