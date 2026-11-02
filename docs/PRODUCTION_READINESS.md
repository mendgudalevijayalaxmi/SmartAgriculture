# Production-readiness review

**Assessment: not production-ready.** This is a code/configuration review, not a penetration test or an independent dependency audit.

## Architecture

The application has a small, clear Spring Boot structure: the REST controller handles HTTP, the service owns crop operations and transactions, Spring Data JPA handles persistence, and DTOs isolate request/response shapes from the entity. PostgreSQL is an appropriate fit for structured crop records. Flyway migrations own schema changes while Hibernate validates the schema. The API has field validation, consistent not-found/validation responses, disabled Open Session in View, and health/readiness probes.

This change adds bounded, paginated crop search and a CI workflow that runs `mvn verify` with read-only repository permissions.

## Findings and priorities

### Critical — API has no authentication or authorization

Every crop endpoint, including create, replace, and delete, is publicly callable. There is no Spring Security dependency or access-control configuration. Anyone who discovers the Render URL can read, alter, or delete records. Do not use this deployment for private or operational farm data. Before production, choose an identity model (for example, OIDC/JWT with roles), protect all crop endpoints, and test unauthorized and cross-user access. This change intentionally does not impose a demo-breaking credential scheme or embed a shared password.

### High — free database is temporary and has no backup

The deployed Render PostgreSQL service is on the free plan. Render documents a 1 GB limit, no backups, expiration after 30 days, and deletion after the 14-day grace period. The three live records are demonstration-only. Before production, use an appropriate paid database plan, configure backup/retention and restore tests, and decide how to migrate the demo data.

### High — deployment source is a feature branch and public-repository connection

The running service uses `feature/spring-boot-crop-api`, not protected `main`. It was connected via Render's public-repository option, which does not provide normal Git-provider auto-deploy integration. Merge reviewed changes to `main`, connect the Git provider with least-privilege repository access, and configure branch protection plus automatic deployment/rollback before production.

### Medium — development datasource defaults could be mistaken for production credentials

The previous shared configuration contained a local fallback password. This change moves those defaults into `application-local.yml`; the base datasource now requires `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD`. Render explicitly activates the `production` profile and supplies these values through its database reference fields. Keep real secrets in the hosting provider, never in Git.

### Medium — unbounded list endpoint

The original `GET /api/v1/crops` returns the entire table. It remains unchanged for compatibility, but the new search endpoint has server-side pagination capped at 100 rows per request. For a larger deployment, migrate clients to that endpoint and consider deprecating the unbounded route.

## Required before production use

1. Add authentication/authorization and define data ownership/tenant boundaries.
2. Move the database to a plan with backups, retention, and a tested restore path.
3. Deploy from protected `main` using a GitHub integration with only the required repository access.
4. Add dependency/SAST scanning, alerting, operational dashboards, and a documented incident/secret-rotation process.
5. Review API data classification, retention, rate limits, and audit requirements with the project owner.
