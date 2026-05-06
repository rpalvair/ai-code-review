# Copilot instructions — ia-code-review

## Goal
Build an AI-powered code review assistant.
Users submit code, the app analyzes it via Anthropic Claude API, and returns **structured feedback** (bugs, security, refactoring, overall quality).

## Repository structure
- `backend/`: Java 25 + Spring Boot 4.0.6 REST API
- `frontend/`: Angular 18+ SPA
- `infrastructure/`: Terraform for AWS
- `.gitlab-ci.yml`: GitLab CI/CD pipeline (repo mirrored GitHub → GitLab)

## Language & conventions
- **Code language:** English
- **Commits/PRs language:** French
- **Backend package:** `com.iacodereview`
- **Endpoints prefix:** `/api`
- **DTOs:** must be separate from JPA entities (never expose entities directly)

## Backend (Spring Boot)
### Stack
- Java 25, Spring Boot 4.0.6
- PostgreSQL via Spring Data JPA / Hibernate
- Liquibase for schema migrations
- Docker (single Dockerfile)
- Direct Anthropic Claude API via HTTP client (NOT AWS Bedrock)

### API contract
#### `POST /api/review`
Request:
```json
{ "code": "string", "language": "string" }
```
Response:
```json
{
  "id": "uuid",
  "language": "string",
  "createdAt": "ISO-8601",
  "feedback": {
    "bugs": ["string"],
    "security": ["string"],
    "refactoring": ["string"],
    "quality": "string",
    "score": 0
  }
}
```

#### `GET /api/review/{id}`
Returns a previously saved review by ID.

### Claude integration (Anthropic)
- **Model:** `claude-sonnet-4-20250514`
- **API key env var:** `ANTHROPIC_API_KEY`
- Claude must return **strict JSON** matching the feedback schema.
- Output must be **JSON only** (no markdown, no extra explanation).

**System prompt (must be kept aligned with expected JSON schema):**
```
You are a senior software engineer performing a code review.
Analyze the provided code and respond ONLY with a valid JSON object (no markdown, no explanation outside JSON) with this exact structure:
{
  "bugs": ["list of identified bugs or potential runtime errors"],
  "security": ["list of security vulnerabilities or risks"],
  "refactoring": ["list of refactoring suggestions for clarity, performance or maintainability"],
  "quality": "a 2-3 sentence overall assessment",
  "score": <integer from 0 to 10>
}
If a category has no findings, return an empty array.
```

### Database
- Liquibase changelogs live in `backend/src/main/resources/db/changelog/`
  - `db.changelog-master.xml` includes other changelogs
  - `changes/v1-init.xml` creates the initial schema
- Schema v1:
```sql
CREATE TABLE reviews (
  id UUID PRIMARY KEY,
  language VARCHAR(50),
  code TEXT,
  feedback JSONB,
  created_at TIMESTAMP DEFAULT NOW()
);
```
- Hibernate strategy: `spring.jpa.hibernate.ddl-auto=validate` (Liquibase owns schema)

### Authentication
- Basic Auth via Spring Security
- Users in memory (no DB table)
- All `/api/**` routes are protected
- Frontend must send `Authorization: Basic base64(login:password)`

Config (single user):
```yaml
spring:
  security:
    user:
      name: ${BASIC_AUTH_USERNAME}
      password: ${BASIC_AUTH_PASSWORD}
```
If multiple users are needed, use an `InMemoryUserDetailsManager` in `SecurityConfig`.

### Environment variables
- `ANTHROPIC_API_KEY`: Claude API key (AWS Secrets Manager in prod)
- `DB_URL`: JDBC URL
- `DB_USERNAME`: DB user
- `DB_PASSWORD`: DB password (Secrets Manager in prod)
- `ALLOWED_ORIGINS`: CORS (CloudFront URL)
- `BASIC_AUTH_USERNAME`: Basic Auth username
- `BASIC_AUTH_PASSWORD`: Basic Auth password

## Frontend (Angular)
- Angular 18+
- Initial UI: textarea input + button + structured rendering of feedback JSON
- Monaco Editor planned for v2 (do not implement unless explicitly requested)

## Infrastructure (AWS via Terraform)
Target architecture (v1):
- ECS Fargate for backend
- ALB in front of ECS (no API Gateway in v1)
- RDS PostgreSQL
- S3 + CloudFront for frontend
- ECR for Docker images
- VPC (private subnets for ECS+RDS, public for ALB)

Terraform lives in `infrastructure/` with modules:
- `modules/vpc`, `modules/ecs`, `modules/rds`, `modules/frontend`

State strategy:
- S3 backend + DynamoDB lock

## CI/CD (GitLab)
- Source repo is on GitHub, mirrored to GitLab
- Pipeline triggers on each push via mirroring
- On push to `main`:
  - Backend: build → test → docker build → push ECR → deploy ECS
  - Frontend: build → deploy S3 → invalidate CloudFront
- Pipeline config: `.gitlab-ci.yml` at repo root

## Scope guardrails
### V1 (in scope)
- Submit code → receive AI review
- DB connected with Liquibase (table created; persistence optional in v1)
- Full AWS infra via Terraform
- GitLab CI/CD for backend + frontend

### V2+ (out of scope unless explicitly requested)
- Review history UI
- Monaco Editor
- Multi-file uploads
- Diff view
- GitHub PR integration
- AWS Bedrock

## Implementation notes (what Copilot should do)
- Prefer small, incremental changes with tests when feasible.
- Keep API responses stable and aligned with the documented schema.
- Validate and sanitize user input; never log secrets.
- When changing DB schema, use Liquibase changesets (no Hibernate auto-DDL).
- Keep frontend-backend contract consistent; update DTOs and Angular types together.
