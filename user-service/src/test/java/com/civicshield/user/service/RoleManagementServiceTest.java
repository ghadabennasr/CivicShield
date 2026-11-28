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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleAuditRepository auditRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoleManagementService roleManagementService;

    private UUID actorId;
    private UUID targetId;
    private User target;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        actorId = UUID.randomUUID();
        targetId = UUID.randomUUID();

        target = new User();
        ReflectionTestUtils.setField(target, "id", targetId);
        target.setEmail("citizen@example.com");
        target.setFirstName("Jane");
        target.setLastName("Doe");
        target.setRole(Role.CITIZEN);

        response = new UserResponse(
                targetId,
                "citizen@example.com",
                "Jane",
                "Doe",
                Role.OPERATOR,
                null
        );
    }

    @Test
    void changeRoleUpdatesUserAndWritesAudit() {
        when(userRepository.findAllByRoleForUpdate(Role.ADMIN)).thenReturn(List.of());
        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(target));
        when(userRepository.save(target)).thenReturn(target);
        when(auditRepository.save(any(UserRoleAudit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userService.toUserResponse(target)).thenReturn(response);

        UserResponse result = roleManagementService.changeRole(
                actorId, targetId, Role.OPERATOR
        );

        assertEquals(Role.OPERATOR, target.getRole());
        assertEquals(Role.OPERATOR, result.role());

        verify(userRepository).save(target);
        verify(auditRepository).save(argThat(audit ->
                audit.getActorId().equals(actorId)
                && audit.getTargetUserId().equals(targetId)
                && audit.getPreviousRole() == Role.CITIZEN
                && audit.getNewRole() == Role.OPERATOR
        ));
    }

    @Test
    void changeRoleRejectsSelfRoleChange() {
        assertThrows(
                SelfRoleChangeException.class,
                () -> roleManagementService.changeRole(
                        actorId, actorId, Role.OPERATOR
                )
        );

        verifyNoInteractions(userRepository, auditRepository);
    }

    @Test
    void changeRoleRejectsDemotionOfLastAdministrator() {
        target.setRole(Role.ADMIN);

        when(userRepository.findAllByRoleForUpdate(Role.ADMIN)).thenReturn(List.of(target));
        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(target));

        assertThrows(
                LastAdministratorException.class,
                () -> roleManagementService.changeRole(
                        actorId, targetId, Role.OPERATOR
                )
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(auditRepository);
    }

    @Test
    void changeRoleRejectsUnknownUser() {
        when(userRepository.findAllByRoleForUpdate(Role.ADMIN)).thenReturn(List.of());
        when(userRepository.findById(targetId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> roleManagementService.changeRole(
                        actorId, targetId, Role.OPERATOR
                )
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(auditRepository);
    }

    @Test
    void assigningSameRoleDoesNotCreateAudit() {
        target.setRole(Role.CITIZEN);

        UserResponse currentResponse = new UserResponse(
                targetId,
                "citizen@example.com",
                "Jane",
                "Doe",
                Role.CITIZEN,
                null
        );

        when(userRepository.findAllByRoleForUpdate(Role.ADMIN)).thenReturn(List.of());
        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(target));
        when(userService.toUserResponse(target))
                .thenReturn(currentResponse);

        UserResponse result = roleManagementService.changeRole(
                actorId, targetId, Role.CITIZEN
        );

        assertEquals(Role.CITIZEN, result.role());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(auditRepository);
    }
}
