package com.civicshield.user.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_role_audits")
public class UserRoleAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID actorId;

    @Column(nullable = false)
    private UUID targetUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role previousRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role newRole;

    @Column(nullable = false, updatable = false)
    private Instant changedAt;

    protected UserRoleAudit() {}

    public UserRoleAudit(
            UUID actorId,
            UUID targetUserId,
            Role previousRole,
            Role newRole) {
        this.actorId = actorId;
        this.targetUserId = targetUserId;
        this.previousRole = previousRole;
        this.newRole = newRole;
    }

    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getActorId() {
        return actorId;
    }

    public UUID getTargetUserId() {
        return targetUserId;
    }

    public Role getPreviousRole() {
        return previousRole;
    }

    public Role getNewRole() {
        return newRole;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}