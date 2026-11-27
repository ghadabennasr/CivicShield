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
