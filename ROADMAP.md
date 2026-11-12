# Roadmap

Spec-first: each milestone starts with updates to [docs/SPEC.md](docs/SPEC.md).

| Milestone | Weeks | Scope | Status |
|---|---|---|---|
| Foundation | — | Spec, curriculum, TESDA mapping, CI, templates, lessons 01–03 | ✅ v0.1 |
| **M1** Stock report | 1 | Lessons 01–04 (methods & arrays closes the week, with a review checkpoint) | ✅ |
| **M2** Interactive console | 2 | Strings, Scanner, ArrayList/HashMap, menu (lessons 05–08) | ✅ |
| **M3** Domain model | 3–4 | Classes, encapsulation, inheritance, interfaces, enums, records, exceptions (lessons 09–16) | ✅ |
| **M4** File persistence | 5 | Generics, streams, files/CSV, `BigDecimal`, `java.time` (lessons 17–20) | ✅ |
| Professional practice | 6 | JUnit/TDD, debugging, Git/GitHub, logging (lessons 21–24) | ✅ |
| **M5** Database-backed | 7–8 | SQL, ERD, JDBC (H2), repository pattern, transactions, migrations (lessons 25–32) | ✅ |
| **M6** REST API | 9–10 | HTTP, REST, JSON, Spring Boot, Spring Data JPA, validation, MockMvc (lessons 33–40) | ✅ |
| Security & quality | 11 | Authentication, roles, CI, OpenAPI (lessons 41–44) | ✅ |
| **M7** Secured, documented, containerised, monitored | 12 | Docker, configuration and deployment, monitoring, capstone and portfolio (lessons 45–48) | ✅ |
| Website readiness | — | `lessons/index.json` generated and checked in CI, README section checker, instructor guide, changelog, code of conduct | ✅ |

**Status: the 48-lesson course is complete.** All lessons and milestones are implemented, and CI checks them on every push.

## Housekeeping to do after the content landed
1. Tag the milestones: `milestone-1` (end of lesson 03 / Week 1), `milestone-2` (lesson 08), `milestone-3` (lesson 16), `milestone-4` (lesson 20), `milestone-5` (lesson 32), `milestone-6` (lesson 40) and `milestone-7` (lesson 48), and a `v1.0.0` release (the website can pin to it).
2. Build the Docker image on a machine with Docker and fix anything the text-only checks of lesson 45 could not see. Update the honest note at the top of lessons 45 and 46.
3. Run the whole course once with a real group; fold their stumbling blocks into the troubleshooting tables and the instructor guide.
4. Review the TESDA mapping and the lesson 48 rubric against the current Training Regulations with a TESDA-accredited trainer.

## Sensible future work (nothing here is promised)
- **PostgreSQL** variant of the app (driver, environment-based URL, migrations proven on it) with **Testcontainers** tests; a lesson on a managed database and backups.
- **Tokens**: JWT / OAuth2 resource server, or Keycloak as the login, as the sequel to lesson 41's HTTP Basic. A "change my password" endpoint and user management.
- **Pagination, sorting and search** for the list endpoints; ETags / optimistic locking for concurrent edits across several copies of the app.
- **Metrics in practice**: Prometheus and Grafana dashboards on top of the Micrometer metrics of lesson 47; a custom business metric (sales per minute).
- **A tiny front end** (plain HTML + `fetch`, then optionally React) that calls the API, as an optional extension.
- **Translations** of the lessons (Filipino, Cebuano, Ilocano and others), starting from the lesson template; `index.json` could carry a language field.
- **Accessibility and offline use**: a printable PDF per week; an offline Maven cache bundle built by CI for classrooms without internet.
- **Windows-first quick start**: a script that installs and checks JDK 21 and Git; `scripts/doctor` that prints what is missing.
- **More assessment material**: quizzes per week, a question bank for oral questioning, sample completed capstones.
- **Automated release notes** from the `CHANGELOG.md` and the lesson index.
- **Cloud lesson (optional)** that deploys the container to one real, free place, written and verified by someone with an account there, with the date it was last checked.
