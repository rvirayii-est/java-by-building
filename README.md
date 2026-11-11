# Java by Building

**Learn Java by creating real applications.** A free, open-source, project-based course: over 12 weeks
(240 hours) you build one **Inventory and Sales Management System**, growing it from a 10-line console
program into a tested, database-backed, deployed REST API you can show to employers.

[![CI](../../actions/workflows/ci.yml/badge.svg)](../../actions/workflows/ci.yml)

- 🧱 **One app, evolving every week** — each milestone is preserved so you can follow its history.
- 🧪 **Tests tell you when you are done** — every lesson has verifiable completion criteria.
- 🆓 **Free tools only** — JDK 21, Git, any editor. No Maven install (wrapper included).
- 🎓 Built for beginners and TESDA trainees; useful to instructors ([curriculum](docs/CURRICULUM.md),
  [TESDA mapping](docs/TESDA-MAPPING.md)).

## Start here
1. Install the tools → [docs/SETUP.md](docs/SETUP.md)
2. Open [Lesson 01 — Hello, Java](lessons/01-hello-java/README.md)
3. Work in `starter/` and `exercises/`; check against the tests; peek at `solution/` last.

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
| 25–48 | See the [curriculum](docs/CURRICULUM.md) and [roadmap](ROADMAP.md) | 🚧 |

## How each lesson folder works
```
lessons/NN-slug/
  README.md   the lesson     examples/   runnable samples (java File.java)
  starter/    app + TODOs    exercises/  small drills + tests
  solution/   reference solution (tests pass)
```
Tests in `starter/` and `exercises/` **fail on purpose** until you finish the TODOs.

## For instructors
Weekly outcomes and checkpoints: [docs/CURRICULUM.md](docs/CURRICULUM.md). Full specification (spec-driven
development): [docs/SPEC.md](docs/SPEC.md). Reference solutions are in each lesson's `solution/`.

## Verify everything (maintainers)
```
./mvnw -B verify                # reference solutions + tests
./mvnw -B -Pstudent test-compile # starters and exercises still compile
bash scripts/run-examples.sh     # every example runs
```

## Contributing & license
See [CONTRIBUTING.md](CONTRIBUTING.md). Licensed under the [MIT License](LICENSE).
