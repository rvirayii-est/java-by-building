# Roadmap

Spec-first: each milestone starts with updates to [docs/SPEC.md](docs/SPEC.md).

| Milestone | Weeks | Scope | Status |
|---|---|---|---|
| Foundation | — | Spec, curriculum, TESDA mapping, CI, templates, lessons 01–03 | ✅ v0.1 |
| **M1** Stock report | 1 | Lessons 01–04 (methods & arrays closes the week, with a review checkpoint) | ✅ lessons 01–04 |
| **M2** Interactive console | 2 | Strings, Scanner, ArrayList/HashMap, menu (lessons 05–08) | ✅ |
| **M3** Domain model | 3–4 | Classes, encapsulation, inheritance, interfaces, enums, records, exceptions (lessons 09–16) | ✅ |
| **M4** File persistence | 5 | Generics, streams, files/CSV, `BigDecimal`, `java.time` (lessons 17–20) | ✅ |
| Professional practice | 6 | JUnit/TDD, debugging, Git/GitHub, logging (lessons 21–24) | ✅ |
| **M5** Database-backed | 7–8 | SQL, ERD, JDBC (H2), repository pattern, transactions, migrations (lessons 25–32) | ✅ |
| **M6** REST API | 9–10 | HTTP, REST, JSON, Spring Boot, Spring Data JPA, MockMvc | 🚧 |
| Security & quality | 11 | Auth, roles, CI, OpenAPI | 🚧 |
| **M7** Deployed | 12 | Docker, deployment, monitoring, capstone, portfolio | 🚧 |

## Next up
1. Tag `milestone-1` (end of lesson 03 / Week 1), `milestone-2` (lesson 08), `milestone-3` (lesson 16), `milestone-4` (lesson 20) and `milestone-5` (lesson 32).
2. Week 9 (HTTP and REST, Spring Boot intro): lessons 33–36, starting from the lesson 32 app. The repositories and `Migrator` carry over: Spring Boot brings Flyway (the professional version of `Migrator`) and Spring Data (generated repositories).
3. Generate `lessons/index.json` from lesson front matter in CI (website integration).
4. Add a Windows `scripts/run-examples.ps1`.
5. Instructor guide (pacing, grading rubric, lab-room setup).
6. Review the TESDA mapping against the current Training Regulations with a TESDA-accredited trainer.
