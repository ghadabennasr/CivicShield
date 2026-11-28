package com.civicshield.user.service;

import com.civicshield.user.dto.UserResponse;
import com.civicshield.user.entity.Role;
import com.civicshield.user.entity.User;
import com.civicshield.user.entity.UserRoleAudit;
import com.civicshield.user.exception.LastAdministratorException;
import com.civicshield.user.exception.SelfRoleChangeException;
import com.civicshield.user.exception.UserNotFoundException;
import com.civicshield.user.repository.UserRepository;
import com.civicshield.user.repository.UserRoleAuditRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RoleManagementService {

    private final UserRepository userRepository;
    private final UserRoleAuditRepository auditRepository;
    private final UserService userService;

    public RoleManagementService(
            UserRepository userRepository,
            UserRoleAuditRepository auditRepository,
            UserService userService) {
        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
        this.userService = userService;
    }

    @Transactional
    public UserResponse changeRole(
            UUID actorId,
            UUID targetUserId,
            Role newRole) {

        if (actorId.equals(targetUserId)) {
            throw new SelfRoleChangeException();
        }

        // Serialize role changes by locking the current administrator rows.
        // This prevents concurrent demotions from removing every administrator.
        java.util.List<User> administrators =
                userRepository.findAllByRoleForUpdate(Role.ADMIN);

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));

        Role previousRole = target.getRole();

        if (previousRole == newRole) {
            return userService.toUserResponse(target);
        }

        if (previousRole == Role.ADMIN
                && newRole != Role.ADMIN
                && administrators.size() <= 1) {
            throw new LastAdministratorException();
        }

        target.setRole(newRole);
        userRepository.save(target);

        UserRoleAudit audit = new UserRoleAudit(
                actorId,
                targetUserId,
                previousRole,
                newRole
        );
        auditRepository.save(audit);

        return userService.toUserResponse(target);
    }
}