
package com.civicshield.user.controller;

import com.civicshield.user.dto.UserResponse;
import com.civicshield.user.entity.User;
import com.civicshield.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal User user) {

        UserResponse response = userService.toUserResponse(user);

        return ResponseEntity.ok(response);
    }
}
