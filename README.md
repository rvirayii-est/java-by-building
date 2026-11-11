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
| 04–48 | See the [curriculum](docs/CURRICULUM.md) and [roadmap](ROADMAP.md) | 🚧 |

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
