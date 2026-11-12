# Changelog

All notable changes to the course. Dates are the dates of the work, newest first. The format follows
[Keep a Changelog](https://keepachangelog.com/); the course does not use semantic versioning for lesson content, only for the Maven
project version (`0.1.0` in the root `pom.xml`), which is not published anywhere.

## [Unreleased] — Stage 4b: the course is complete (lessons 41-48, milestone M7)

### Added
- **Lesson 41 Authentication basics**: Spring Security with HTTP Basic, users in the database (migration V4), BCrypt hashes, JSON `401`; `GET /me`. Honest note on tokens/JWT as the next step.
- **Lesson 42 Roles**: `ADMIN` and `CASHIER`, the rules table in `SecurityConfig`, JSON `403`, migration V5 on a table that has rows; a matrix test, slice tests with `@WithMockUser`, a migration test.
- **Lesson 43 CI pipeline**: this repository's `ci.yml` explained line by line; a student workflow (`ci/student-ci.yml`) tested with SnakeYAML; Maven cache, badge, safe habits.
- **Lesson 44 API docs**: springdoc OpenAPI 2.8.17 (the line for Spring Boot 3.5), `/v3/api-docs` and Swagger UI, error shapes, a test against the design table.
- **Lesson 45 Docker**: multi-stage `Dockerfile`, root `.dockerignore`, `compose.yaml`, graceful shutdown; the files are tested as text. **No image was built** (Docker was not installed where the lesson was written).
- **Lesson 46 Deploy**: `prod` profile and environment variables, validated `SecuritySettings` (the app refuses weak or missing passwords), `.env.example`, honest hosting options. Nothing was deployed.
- **Lesson 47 Monitoring**: Actuator (`health` with liveness and readiness, `info`, `metrics`), secured exposure, `RequestIdFilter`, structured JSON logs in `prod`.
- **Lesson 48 Capstone and portfolio**: milestone **M7**; `GET /reports/low-stock` as the worked example; a real-HTTP end-to-end scenario with security; capstone brief, ten-criterion rubric, portfolio README template, 5-minute demo script, resume bullets.
- `lessons/index.json` generated from the lesson front matter, and `scripts/LessonTool.java` (`check`, `index`, `index --check`); the contract is in `docs/WEBSITE-INTEGRATION.md`.
- `docs/INSTRUCTOR-GUIDE.md`: pacing per week, lab setup (offline and slow-network tips), assessment, using solutions, common blockers.
- `scripts/run-examples.ps1`, the PowerShell twin of `run-examples.sh` (tested in Windows PowerShell 5.1 and PowerShell 7).
- `CHANGELOG.md`, `CODE_OF_CONDUCT.md`.

### Changed
- CI: least-privilege `permissions`, `concurrency`, a timeout, the two lesson checks first, test reports uploaded when a step fails.
- `README.md`: a clear learner path and the description of M7. `docs/CURRICULUM.md`, `docs/SPEC.md`, `docs/TESDA-MAPPING.md`, `ROADMAP.md`: all 48 lessons marked done.
- `docs/SETUP.md`: login, GitHub account and optional Docker notes.
- Root `pom.xml`: `springdoc.version` and its managed dependency; modules for lessons 41-48.
- `.gitignore`: `.env`.

### Known limits (see ROADMAP)
- Docker and compose files not exercised with a real Docker. No deployment to a real provider.
- HTTP Basic, not tokens. H2 file database, not PostgreSQL.

## Earlier work
- **Stage 4a, lessons 33-40, milestone M6**: HTTP, REST design, JSON, Spring Boot, controllers and services, Spring Data JPA and Flyway, validation and one error shape, MockMvc.
- **Stage 3, lessons 25-32, milestone M5**: relational thinking, SQL, joins, schema design, JDBC, repository pattern, transactions, migrations.
- **Stage 2, lessons 17-24, milestone M4 and Week 6**: generics, streams, files, `BigDecimal` and `java.time`, JUnit and TDD, debugging, Git and GitHub, logging.
- **Stage 1, lessons 01-16, milestones M1-M3**: Java foundations, collections, the console menu, OOP, exceptions.
- **Foundation, v0.1**: spec, curriculum, TESDA mapping, CI, lesson template.
