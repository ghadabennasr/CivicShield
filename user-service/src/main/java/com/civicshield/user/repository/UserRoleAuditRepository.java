package com.civicshield.user.repository;

import com.civicshield.user.entity.UserRoleAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRoleAuditRepository
        extends JpaRepository<UserRoleAudit, UUID> {
}