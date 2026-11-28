# CivicShield

Secure citizen incident reporting platform. Project owners: Ghada Ben Nasr and Fares Klai.

## Services

| Service | Style |
| --- | --- |
| api-gateway | REST |
| user-service | REST |
| report-service | REST |
| case-service | REST |
| correlation-service | gRPC |
| abuse-service | gRPC |
| municipal-legacy-sim | SOAP |

## Team workflow

- No direct push to `main`. Every change goes through a pull request approved by the other person.
- Branches: `feat/<service>-<topic>`, `fix/`, `test/`, `docs/`, `chore/`.
- Commits follow Conventional Commits, e.g. `feat(report-service): call correlation via gRPC`.
- Merge with a merge commit (no squash) so small commits stay visible.

## How to run

Coming soon (Docker Compose).

## Technology and architecture

- Spring Boot 3.3 and Java 17.
- PostgreSQL with a database-per-service approach.
- REST, gRPC, and SOAP communication.
- JWT-based authentication and role-based authorization.

## Environment configuration

1. Copy .env.example to .env.
2. Replace placeholder credentials.
3. Set JWT_SECRET to a securely generated Base64-encoded random value.
4. The decoded JWT secret must contain at least 32 bytes for HS256.
5. Never commit .env or share production secrets.

Generate a secure secret in PowerShell:

    $bytes = New-Object byte[] 32
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($bytes)
    $env:JWT_SECRET = [Convert]::ToBase64String($bytes)
    $rng.Dispose()
    Remove-Variable bytes, rng

## Current development status

The user-service provides registration, login, JWT authentication, current-user retrieval, and administrator-only role management. Role changes are recorded in an audit table.
