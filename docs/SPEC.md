# Course Specification — Java by Building

Status: **v0.1 (draft, living document).** Spec comes first: lessons are written against this file.
Change the spec in a PR *before* changing lessons that depend on it.

## 1. Purpose

A free, open-source, project-based Java course. Students learn Java by building **one application that
evolves every week**: an **Inventory and Sales Management System** for a small shop (sari-sari store →
multi-user web service). They graduate with a tested, deployed application and a Git history they can
show to employers.

## 2. Audience

| Audience | Needs |
|---|---|
| Absolute beginners, incl. TESDA trainees | No assumed programming background. Plain English. Free tools only. Works on a modest PC. |
| Career switchers / self-paced learners | Every lesson stands alone: read, build, check with tests. |
| Instructors / classrooms | Schedule, learning outcomes, assessable checkpoints, reference solutions. |

Prerequisites: basic computer literacy, English reading, ability to install software. No prior coding.

## 3. Requirements

### Functional (course)
- **F1** 12 weeks, **240 hours** (20 h/week: ~8 h guided lesson, ~12 h lab/exercises/project).
- **F2** Progression: Java fundamentals → OOP → collections/files → testing → databases → REST/Spring Boot → deployment.
- **F3** One evolving application. Each milestone is a runnable, tested snapshot that is preserved (§6).
- **F4** Every lesson contains the eight required parts (§5).
- **F5** Starter code, exercises and reference solutions live in separate directories (§6).
- **F6** Competency mapping to TESDA Programming (Java) NC III ([TESDA-MAPPING.md](TESDA-MAPPING.md)).
- **F7** Lessons are individually linkable/embeddable from another website ([WEBSITE-INTEGRATION.md](WEBSITE-INTEGRATION.md)).

### Non-functional
- **N1** Java **21 LTS**. Revisit when the next LTS is stable and widely packaged.
- **N2** Free tools only: JDK (OpenJDK/Temurin), a text editor or free IDE (VS Code, IntelliJ IDEA Community), Git.
  Early lessons need only a JDK and a terminal. Maven is added through the bundled `./mvnw` wrapper (no install).
  From lesson 25 the **H2** database (a single free `.jar`, in memory for tests and in a file for the app) is the only
  extra dependency. Maven downloads it; there is no database server to install, and PostgreSQL/MySQL are explained, never required.
- **N3** Every published code example compiles and runs. CI enforces it (§8).
- **N4** Frameworks arrive late and only when the pain they solve has been felt (no Spring before week 9).
- **N5** No paid services, accounts or cloud credit required before week 12 (the deployment lesson offers free options).
  No database server, account or installer is needed in weeks 7-8: H2 runs inside the Java program.
- **N6** Beginner-friendly: short sentences, define every new term on first use, show expected output.

### Application requirements (the Inventory & Sales System)
| ID | Capability | First appears |
|---|---|---|
| A1 | Show a stock report with LOW / OUT status | Week 1 (M1) |
| A2 | Price a sale line with VAT, discounts, change | Week 1 (M1) |
| A3 | Interactive console menu: add product, receive stock, record sale | Week 2 (M2) |
| A4 | Domain model: Product, Customer, Sale, SaleLine, StockMovement | Weeks 3–4 (M3) |
| A5 | Save/load data to files | Week 5 (M4) |
| A6 | Relational database persistence (H2, JDBC, repositories) with transactions and versioned migrations | Weeks 7–8 (M5) |
| A7 | REST API (products, sales, reports) | Weeks 9–10 (M6) |
| A8 | Authentication and roles (admin, cashier) | Week 11 |
| A9 | Containerised, deployed, monitored service | Week 12 (M7) |

Money is stored as integer **centavos** until lesson 20 (milestone M4), which replaces it with `BigDecimal` pesos and adds `java.time` timestamps.

## 4. Learning outcomes
By week 12 a student can: (1) write, run and debug Java programs; (2) model a problem with classes and
interfaces; (3) use collections, streams, files and exceptions; (4) write automated tests; (5) design a
relational schema and query it with SQL/JDBC; (6) build and test a REST API with Spring Boot;
(7) use Git/GitHub and CI; (8) package and deploy a service; (9) explain their design to others.

## 5. Lesson contract
Every lesson `README.md` MUST contain, in this order:

1. **Learning objectives** — verbs a test can check ("write", "explain", "fix").
2. **Plain-English explanation** — concepts without jargon; new terms defined.
3. **Runnable sample code** — in `examples/`, run with `java File.java` (lessons 25-32 add `-cp <H2 jar>`); CI-verified.
4. **Step-by-step instructions** — numbered build steps for the app milestone.
5. **Practical exercises** — small drills in `exercises/` (with tests).
6. **Expected results** — exact output or test counts to compare against.
7. **Common mistakes and troubleshooting** — symptom → cause → fix.
8. **Verifiable completion criteria** — the commands that must pass (`./mvnw test` …) plus a checklist.

Lessons begin with YAML front matter (`title`, `slug`, `week`, `hours`, `level`, `milestone`, `objectives`).
A blank template is in [`templates/LESSON_TEMPLATE.md`](../templates/LESSON_TEMPLATE.md).

## 6. Repository layout

```
lessons/NN-slug/
  README.md        the lesson (front matter + 8 parts)
  examples/        single-file runnable samples:  java HelloJava.java
  starter/         the app as it was at the end of the previous lesson, + TODOs. Tests FAIL until done.
  exercises/       small standalone drills + tests. Tests FAIL until done.
  solution/        reference solution (cumulative app + solved exercises). Tests PASS (CI-enforced).
docs/              spec, curriculum, TESDA mapping, setup, integration notes
templates/         LESSON_TEMPLATE.md
```
Each `starter|exercises|solution` is a Maven module. **Milestones** are preserved by the cumulative
`solution/` of each lesson and by Git tags (`milestone-1`, …). Students can diff
`lessons/02-…/solution` against `lessons/03-…/solution` to see exactly how the app evolved.

## 7. Acceptance criteria for a lesson (definition of done)
- [ ] Front matter and all 8 parts present
- [ ] `examples/*.java` run with the documented command and match "Expected results"
- [ ] `solution/` builds and its tests pass: `./mvnw -B verify`
- [ ] `starter/` and `exercises/` compile (`./mvnw -Pstudent test-compile`) and their tests fail meaningfully
- [ ] Reviewed for plain English (a beginner could follow it without outside help)

## 8. CI
GitHub Actions (`.github/workflows/ci.yml`): JDK 21, `./mvnw verify` (solutions + tests),
`./mvnw -Pstudent test-compile` (starters/exercises compile), `scripts/run-examples.sh` (every example runs; it puts the H2 jar on the class path for lessons 25-32).

## 9. Out of scope (for now)
Front-end frameworks, microservices, cloud-vendor specifics, Kotlin/Android. A minimal HTML client may be
added as an optional extension.
