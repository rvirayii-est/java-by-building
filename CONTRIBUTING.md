# Contributing

Thank you for helping beginners learn Java! All contributions are welcome: fixes, clearer wording,
new lessons, translations, instructor notes.

## Ground rules
- **Spec first.** Propose new lessons or changes to scope by editing [docs/SPEC.md](docs/SPEC.md) /
  [docs/CURRICULUM.md](docs/CURRICULUM.md) in an issue or PR before writing lesson content.
- **Plain English.** Short sentences. Define new terms. Assume no prior knowledge.
- **Everything runs.** Every code example must compile and run. CI checks it.
- **Free tools only** for early lessons; add frameworks only when the curriculum says so.
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
   bash scripts/run-examples.sh
   ```
5. Confirm starter/exercise tests **fail** until TODOs are done: `./mvnw -Pstudent test -pl lessons/NN-slug/starter`.
6. Tick the checklist in [docs/SPEC.md §7](docs/SPEC.md) in your PR description.

## Commits and PRs
- Small, focused PRs. Imperative commit messages (`feat: add lesson 04`, `fix: typo in lesson 02`).
- Never rename a published lesson folder (slugs are stable URLs).

## Reporting problems
Open an issue with: lesson, what you did, what you expected, what happened (paste the error), and `java -version`.

## Code of conduct
Be kind and patient; many learners are brand new. Harassment or condescension is not tolerated.
