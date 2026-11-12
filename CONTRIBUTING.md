# Contributing

Thank you for helping beginners learn Java! All contributions are welcome: fixes, clearer wording,
new lessons, translations, instructor notes.

## Ground rules
- **Spec first.** Propose new lessons or changes to scope by editing [docs/SPEC.md](docs/SPEC.md) /
  [docs/CURRICULUM.md](docs/CURRICULUM.md) in an issue or PR before writing lesson content.
- **Plain English.** Short sentences. Define new terms. Assume no prior knowledge.
- **Everything runs.** Every code example must compile and run. CI checks it.
- **Free tools only** for early lessons; add frameworks only when the curriculum says so.
- **SQL in a README must be real.** From lesson 25 on, every `sql` block in a lesson README is run against H2 by `ReadmeSqlTest`
  and the `text` block under it must match the output exactly (see the comment at the top of that test for the few rules).
- **HTTP examples must be real.** From lesson 33 on, every `curl` command and its answer in a README comes from a real run of the lesson's server, and the test counts are copied from a real `./mvnw test`.
- **Libraries are introduced where they are taught** (Jackson in 35, Spring Boot in 36, JPA and Flyway in 38, validation in 39, Spring Security in 41, springdoc in 44, Actuator in 47). Versions live only in the root `pom.xml` (the Spring Boot BOM, plus `springdoc.version`): a lesson `pom.xml` names a library, never a version.
- **Be honest about what was not run.** If a step needs a tool you could not run (Docker, a real hosting account), say so in the lesson, test what you can as text, and never write an output that did not come from a real run.
- **Front matter has seven keys**: `title`, `slug` (equal to the folder name), `week`, `hours`, `level` (`beginner`, `intermediate` or `advanced`), `milestone`, `objectives` (a list). The 8 parts are the headings `## 1. ` to `## 8. `. `java scripts/LessonTool.java check` verifies both; CI runs it.
- Java **21**. No preview features.

## Adding or changing a lesson
1. Copy [`templates/LESSON_TEMPLATE.md`](templates/LESSON_TEMPLATE.md) to `lessons/NN-slug/README.md`.
2. Create `examples/`, `starter/`, `exercises/`, `solution/` (see an existing lesson).
   `solution/` is the previous solution **plus** your changes; `starter/` is the same with the new code replaced by TODO stubs.
3. Add the new modules to the root `pom.xml` (`solution` in `<modules>`, `starter`/`exercises` in the `student` profile).
4. Verify locally:
   ```
   ./mvnw -B verify
   ./mvnw -B -Pstudent test-compile
   bash scripts/run-examples.sh        # Windows without Git Bash: scriptsun-examples.ps1
   java scripts/LessonTool.java check          # front matter and the 8 sections of every lesson
   java scripts/LessonTool.java index          # regenerate lessons/index.json; commit it (CI fails if it is stale)
   ```
5. Confirm starter/exercise tests **fail** until TODOs are done: `./mvnw -Pstudent test -pl lessons/NN-slug/starter`.
6. Tick the checklist in [docs/SPEC.md §7](docs/SPEC.md) in your PR description.

## Commits and PRs
- Small, focused PRs. Imperative commit messages (`feat: add lesson 04`, `fix: typo in lesson 02`).
- Never rename a published lesson folder (slugs are stable URLs).

## Reporting problems
Open an issue with: lesson, what you did, what you expected, what happened (paste the error), and `java -version`.

## Code of conduct
Be kind and patient; many learners are brand new. Harassment or condescension is not tolerated. See [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
