# Java by Building

**Learn Java by creating real applications.** A free, open-source, project-based course: over 12 weeks
(240 hours) you build one **Inventory and Sales Management System**, growing it from a 10-line console
program into **Milestone M7**: a tested, database-backed REST API that is secured (logins and roles), documented (OpenAPI),
containerised (Docker), configured from the environment and monitored (health checks, request ids, structured logs). You finish with a
portfolio README, a 5-minute demo and resume bullets you can show to employers.

[![CI](../../actions/workflows/ci.yml/badge.svg)](../../actions/workflows/ci.yml)

- 🧱 **One app, evolving every week** — each milestone is preserved so you can follow its history.
- 🧪 **Tests tell you when you are done** — every lesson has verifiable completion criteria.
- 🆓 **Free tools only** — JDK 21, Git, any editor. No Maven install (wrapper included). The database (H2) is one free `.jar`, and from Week 9 Spring Boot is a set of free libraries: Maven downloads them, nothing to install. Docker is optional and no hosting account is ever required.
- 🎓 Built for beginners and TESDA trainees; useful to instructors ([curriculum](docs/CURRICULUM.md),
  [instructor guide](docs/INSTRUCTOR-GUIDE.md), [TESDA mapping](docs/TESDA-MAPPING.md)).

## Start here: the learner path
1. **Install the tools** (a JDK 21, Git, an editor, `curl`; about 20 minutes) → [docs/SETUP.md](docs/SETUP.md)
2. **Get the course**: `git clone https://github.com/rvirayii-est/java-by-building.git` (or download the zip), then open the folder in your editor.
3. **Open [Lesson 01 — Hello, Java](lessons/01-hello-java/README.md)** and follow the lessons **in order**. A lesson is about 5 hours: read, run the examples, build in `starter/`, do the drills in `exercises/`.
4. **Let the tests guide you.** Tests in `starter/` and `exercises/` fail on purpose; run `../../../mvnw test` (Windows: `..\..\..\mvnw.cmd test`) inside the folder until they are green. Every lesson ends with a checklist.
5. **Peek at `solution/` last**, then close it and write the code again from memory.
6. **Follow the milestones**: M1 stock report (lesson 03), M2 console app (08), M3 domain model (16), M4 files (20), M5 database (32), M6 REST API (40), **M7 the finished secured, documented, containerised, monitored API (48)**.
7. **Week 12: the capstone.** Add one feature of your own, put it on GitHub with a green CI badge, record the 5-minute demo. Lesson 48 has the brief, the README template and the demo script.

Stuck? Each lesson has a troubleshooting table. Still stuck? Open an issue (see [CONTRIBUTING.md](CONTRIBUTING.md)): say the lesson, what you did, what you expected, what happened, and `java -version`.
New to the terminal? Ten minutes of `cd` and `ls` (or `dir`) in week 1 will pay back all course.

## Lessons
| # | Lesson | Status |
|---|---|---|
| 01 | [Hello, Java](lessons/01-hello-java/README.md) | ✅ |
| 02 | [Variables, types and money](lessons/02-variables-and-money/README.md) | ✅ |
| 03 | [Decisions and loops](lessons/03-decisions-and-loops/README.md) — **Milestone M1** | ✅ |
| 04 | [Methods and arrays](lessons/04-methods-and-arrays/README.md) — Week 1 review | ✅ |
| 05 | [Strings and text](lessons/05-strings-and-text/README.md) | ✅ |
| 06 | [Scanner and input validation](lessons/06-scanner-and-input/README.md) | ✅ |
| 07 | [ArrayList and HashMap](lessons/07-arraylist-and-hashmap/README.md) | ✅ |
| 08 | [Console menu](lessons/08-console-menu/README.md) — **Milestone M2** | ✅ |
| 09 | [Classes and objects](lessons/09-classes-and-objects/README.md) | ✅ |
| 10 | [Encapsulation](lessons/10-encapsulation/README.md) | ✅ |
| 11 | [Product and Customer classes](lessons/11-product-and-customer/README.md) | ✅ |
| 12 | [Static vs instance](lessons/12-static-vs-instance/README.md) — Week 3 checkpoint | ✅ |
| 13 | [Inheritance](lessons/13-inheritance/README.md) | ✅ |
| 14 | [Interfaces and polymorphism](lessons/14-interfaces-and-polymorphism/README.md) | ✅ |
| 15 | [Enums and records](lessons/15-enums-and-records/README.md) | ✅ |
| 16 | [Exceptions](lessons/16-exceptions/README.md) — **Milestone M3** | ✅ |
| 17 | [Generics and collections](lessons/17-generics-and-collections/README.md) | ✅ |
| 18 | [Streams](lessons/18-streams/README.md) | ✅ |
| 19 | [Files and CSV](lessons/19-files-and-csv/README.md) | ✅ |
| 20 | [BigDecimal and java.time](lessons/20-bigdecimal-and-time/README.md) — **Milestone M4** | ✅ |
| 21 | [JUnit and TDD](lessons/21-junit-and-tdd/README.md) | ✅ |
| 22 | [Debugging](lessons/22-debugging/README.md) | ✅ |
| 23 | [Git and GitHub flow](lessons/23-git-and-github/README.md) | ✅ |
| 24 | [Logging and clean code](lessons/24-logging-and-clean-code/README.md) — Week 6 complete | ✅ |
| 25 | [Relational thinking and ERD](lessons/25-relational-thinking/README.md) | ✅ |
| 26 | [SQL basics](lessons/26-sql-basics/README.md) | ✅ |
| 27 | [Joins and reports](lessons/27-joins-and-reports/README.md) | ✅ |
| 28 | [Schema design for the shop](lessons/28-schema-design/README.md) — Week 7 complete | ✅ |
| 29 | [JDBC](lessons/29-jdbc/README.md) | ✅ |
| 30 | [The repository pattern](lessons/30-repository-pattern/README.md) | ✅ |
| 31 | [Transactions](lessons/31-transactions/README.md) | ✅ |
| 32 | [Migrations](lessons/32-migrations/README.md) — **Milestone M5** | ✅ |
| 33 | [How the web works](lessons/33-how-the-web-works/README.md) | ✅ |
| 34 | [REST design](lessons/34-rest-design/README.md) | ✅ |
| 35 | [JSON](lessons/35-json/README.md) | ✅ |
| 36 | [Spring Boot "Hello"](lessons/36-spring-boot-hello/README.md) | ✅ |
| 37 | [Controllers and services](lessons/37-controllers-and-services/README.md) | ✅ |
| 38 | [Spring Data JPA](lessons/38-spring-data-jpa/README.md) | ✅ |
| 39 | [Validation and errors](lessons/39-validation-and-errors/README.md) | ✅ |
| 40 | [Testing with MockMvc](lessons/40-testing-with-mockmvc/README.md) — **Milestone M6** | ✅ |
| 41 | [Authentication basics](lessons/41-auth-basics/README.md) | ✅ |
| 42 | [Roles: admin and cashier](lessons/42-roles/README.md) | ✅ |
| 43 | [A CI pipeline with GitHub Actions](lessons/43-ci-pipeline/README.md) | ✅ |
| 44 | [API documentation with OpenAPI](lessons/44-api-docs/README.md) | ✅ |
| 45 | [Docker: ship the API in a container](lessons/45-docker/README.md) | ✅ |
| 46 | [Deploy: configuration and where the app lives](lessons/46-deploy/README.md) | ✅ |
| 47 | [Monitoring: health checks, metrics and logs](lessons/47-monitoring/README.md) | ✅ |
| 48 | [Capstone and portfolio](lessons/48-capstone/README.md) — **Milestone M7** | ✅ |

## How each lesson folder works
```
lessons/NN-slug/
  README.md   the lesson     examples/   runnable samples (java File.java)
  starter/    app + TODOs    exercises/  small drills + tests
  solution/   reference solution (tests pass)
```
Tests in `starter/` and `exercises/` **fail on purpose** until you finish the TODOs.

## For instructors
Start with the [instructor guide](docs/INSTRUCTOR-GUIDE.md): pacing per week, assessment rubric, lab setup (including offline and slow-network tips), how to use the solutions, and the blockers students hit most. Weekly outcomes and checkpoints:
[docs/CURRICULUM.md](docs/CURRICULUM.md). Full specification (spec-driven development): [docs/SPEC.md](docs/SPEC.md). Competency mapping (indicative): [docs/TESDA-MAPPING.md](docs/TESDA-MAPPING.md). Reference solutions are in each lesson's `solution/`; the capstone rubric is in lesson 48.

## Verify everything (maintainers)
```
java scripts/LessonTool.java check        # every lesson README has its front matter and 8 numbered sections
java scripts/LessonTool.java index --check # lessons/index.json is up to date (write it with:  java scripts/LessonTool.java index)
./mvnw -B verify                # reference solutions + tests
./mvnw -B -Pstudent test-compile # starters and exercises still compile
bash scripts/run-examples.sh     # every example runs (Windows without Git Bash: scripts\run-examples.ps1)
```
CI runs exactly these (`.github/workflows/ci.yml`, explained line by line in [lesson 43](lessons/43-ci-pipeline/README.md)).
For a website: [docs/WEBSITE-INTEGRATION.md](docs/WEBSITE-INTEGRATION.md) describes `lessons/index.json`.

## Contributing & license
See [CONTRIBUTING.md](CONTRIBUTING.md), the [Code of Conduct](CODE_OF_CONDUCT.md) and the [changelog](CHANGELOG.md). Licensed under the [MIT License](LICENSE).
