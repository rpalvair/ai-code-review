# Copilot instructions — ia-code-review

## Language & conventions
- **Code language:** English
- **Commits/PRs language:** French
- **Backend package:** `com.iacodereview`
- **Endpoints prefix:** `/api`
- **DTOs:** must be separate from JPA entities (never expose entities directly)

## Backend (Spring Boot)
- Java 25, Spring Boot 4.0.6
- Use Liquibase changesets for all schema changes (no Hibernate auto-DDL)
- `spring.jpa.hibernate.ddl-auto=validate`
- Basic Auth via Spring Security; all `/api/**` routes are protected
- Never expose JPA entities directly in API responses

## Claude integration (Anthropic)
- Model: `claude-sonnet-4-20250514`
- Claude must return **strict JSON only** (no markdown, no extra explanation)
- Keep the system prompt aligned with the expected feedback JSON schema

## Frontend (Angular)
- Angular 18+
- Monaco Editor is out of scope unless explicitly requested

## Implementation notes
- Prefer small, incremental changes with tests when feasible
- Keep API responses stable and aligned with the documented schema
- Validate and sanitize user input; never log secrets
- Keep frontend-backend contract consistent; update DTOs and Angular types together
