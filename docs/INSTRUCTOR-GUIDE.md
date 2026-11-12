# Instructor guide

For teachers, trainers and anyone running Java by Building with a group. It assumes you have read the [curriculum](CURRICULUM.md).
The course is also fine for self-study; this guide is about running it with people in a room.

> **Honest scope.** This guide is a practical starting point written from the structure of the repository, not from a classroom
> trial. Adjust pacing to your group. The TESDA parts are *indicative*: check them against the current Training Regulations and
> your assessment centre's procedures ([TESDA-MAPPING.md](TESDA-MAPPING.md)).

## 1. The shape of the course
- 12 weeks, 48 lessons, about 5 hours per lesson (guided time plus lab), 20 hours per week, 240 hours.
- One application grows all the way: a console stock report (M1), an interactive menu (M2), a domain model (M3), file persistence (M4),
  a database (M5), a REST API (M6) and a secured, documented, containerised, monitored service (M7).
- Every lesson has the same eight parts, so students always know where they are: objectives, explanation, runnable examples,
  step-by-step, exercises, expected results, troubleshooting, completion criteria.
- **The tests are the teacher's assistant.** `starter/` and `exercises/` fail on purpose; green means done. A student can check their
  own work without waiting for you. `solution/` is always green.

## 2. Pacing per week

A suggested split of a 20-hour week (adapt freely). **Guided** = you teach or live-code; **lab** = students work, you circulate.

| Wk | Lessons | Guided | Lab | Checkpoint to look for (see CURRICULUM) | Watch for |
|---|---|---|---|---|---|
| 1 | 01-04 Java foundations | 8 h | 12 h | Student runs M1 and explains every line of the stock report; lesson tests green | Installation problems eat day 1: do them together, early |
| 2 | 05-08 Strings, input, collections | 7 h | 13 h | M2 interactive menu works | `Scanner` leftovers (`nextInt` then `nextLine`); off-by-one in loops |
| 3 | 09-12 OOP I | 7 h | 13 h | Student writes a class with a constructor and private fields | "Why not make everything `public static`?" Give it time |
| 4 | 13-16 OOP II | 7 h | 13 h | A new entity (for example `Supplier`) without help (M3) | Inheritance overuse; exceptions swallowed silently |
| 5 | 17-20 Collections, streams, files, `BigDecimal` | 7 h | 13 h | M4 saves and loads files | Streams are a stumbling block: let students write the loop first |
| 6 | 21-24 Testing, debugging, Git, logging | 7 h | 13 h | A failing test fixed by TDD and submitted as a pull request | Git: merge conflicts. Run one on purpose in class |
| 7 | 25-28 Databases I (SQL, joins, schema) | 7 h | 13 h | Schema designed and tested | Joins: draw the tables on paper first |
| 8 | 29-32 Databases II (JDBC, repository, transactions, migrations) | 7 h | 13 h | A student explains a transaction and why a sale needs one (M5) | Resource leaks; "why can't I edit V1?" |
| 9 | 33-36 HTTP, REST, JSON, Spring Boot | 7 h | 13 h | `curl` against their own server | Port 8080 already in use; the "magic" of Spring: show lesson 33 again |
| 10 | 37-40 Spring layers, JPA, validation, MockMvc | 7 h | 13 h | A new endpoint with its tests (M6) | JPA surprises (lazy loading, `ddl-auto`); too many full-application tests |
| 11 | 41-44 Security, roles, CI, OpenAPI | 7 h | 13 h | Student explains 401 versus 403 and shows a green CI run on their own repository | The rule order in `SecurityConfig`; YAML indentation; a GitHub account is needed from lesson 43 |
| 12 | 45-48 Docker, deploy, monitoring, capstone | 6 h | 14 h | Capstone demo: working app, README, 5-minute walkthrough (M7) | Docker may not be installable on lab PCs (see section 4); keep the capstone **small** |

Weeks 1, 6 and 12 include a review block: keep a half day free in those weeks. If you must cut, cut **breadth, not tests**: for
example skip the stretch exercises, never the completion criteria.

If a group falls behind: lessons 33-35 (the web from scratch) can be shortened to demonstrations if time is short; everything from
lesson 36 builds on the Spring versions, but students still need the idea that a request is text and a response is text.

## 3. A lesson, hour by hour (a template)
1. **0:00-0:30** Read objectives together; ask one question to find out what they already know.
2. **0:30-1:30** Live-code the example and the first step of the build. Let students type along; make a mistake on purpose.
3. **1:30-4:00** Lab: starter TODOs, then exercises. You circulate. Pair up weaker and stronger students and swap the keyboard.
4. **4:00-4:45** Compare with "Expected results" and the troubleshooting table. Collect the questions that came up twice.
5. **4:45-5:00** Exit ticket: each student writes one thing they can now do and one thing that is still unclear.

## 4. Lab setup

### What every machine needs
JDK 21, Git, a text editor or IDE (VS Code or IntelliJ IDEA Community), a terminal, `curl` (Git Bash has it). See
[SETUP.md](SETUP.md). No Maven installation: the wrapper `./mvnw` downloads Maven once. Docker is needed **only** for the
optional parts of lessons 45-46; lesson 48 can be completed without it (the rubric says how to assess that).

### Hardware
A modest PC is enough: 8 GB RAM is comfortable, 4 GB works for lessons 1-32. From lesson 36 a Spring Boot test run starts several
applications: about 20-30 seconds per lesson on the author's laptop (measured), several times that on a slow PC. Close browsers while running tests on a low-memory PC.

### Accounts
Only from lesson 23 (GitHub, free) and lesson 43 (GitHub Actions, free for public repositories). No paid account is needed anywhere.
Under-age learners may need a parent's consent to create a GitHub account: check your institution's rules before week 6.

### Offline and slow-network tips
The first Maven build downloads Spring Boot and its libraries plus the Maven distribution itself: a clean Maven cache for the finished application (lesson 48, tests included) measured about 100 MB on the author's machine, plus about 10 MB for Maven; budget 150 MB per student for the whole course. On a slow or
metered network this is the biggest risk of the course. Options, cheapest first:
1. **Warm the cache once, copy it.** On one machine with good internet run `./mvnw -B -Pstudent test-compile` (this resolves everything
   the course uses), then copy the folder `~/.m2/repository` (on Windows `%USERPROFILE%\.m2\repository`) and the Maven wrapper
   distribution `~/.m2/wrapper` to a USB stick or a shared folder. Copy them into each student's home folder before the lesson.
   Students then build offline: `./mvnw -o -B test`.
2. **A shared Maven mirror** (Nexus, Artifactory or a simple caching proxy) on the lab network, and one `~/.m2/settings.xml` that points to it.
3. **Pre-download per week.** Run the next week's lesson build the evening before, in the lab, while the network is quiet.
4. **Docker images** (lessons 45-46) are 200+ MB each. Pull `eclipse-temurin:21-jdk` and `eclipse-temurin:21-jre` in advance, or skip Docker and use the "explain each line" alternative of the rubric.
5. **Run examples and tests offline** once the cache is warm: `./mvnw -o -B verify`. If Maven says it cannot resolve something offline, the cache is missing that artifact: run the same command online once.
6. Keep the **JDK installers**, **Git**, **VS Code** and the **repository as a zip** on a USB stick. The course needs nothing else.

### Shared machines
Students on shared PCs should keep their work in their own folder and push to GitHub often: a re-imaged lab PC loses everything.
Tests use in-memory databases, but the app writes `shop-data/` in the folder it is started from: tell students to start it in their project folder, and not to commit it (`.gitignore` already skips it).

### Ports
The app uses 8080. Two students on one machine, or a leftover process, cause `Port 8080 already in use`. Fix: stop the other one, or start with `--server.port=9090` (lesson 46 explains why that works).

## 5. Assessment

### Continuous
- Every lesson's **completion criteria** are objective: commands that pass, a checklist. Collect "all green" as the baseline.
- Use the **weekly checkpoints** in the curriculum as short oral checks: ask the student to explain, not only to run.
- Never accept "it works on my PC" in weeks 11-12: the CI badge is the evidence.

### Rubric for the weekly work (suggested)
| Level | Meaning |
|---|---|
| 3 Exceeds | Completion criteria met; student explains choices; extends a stretch exercise or helps a peer |
| 2 Meets | Completion criteria met; student answers the checkpoint question |
| 1 Partly | Some criteria met; needs help to finish; explanation is shaky |
| 0 No evidence | Nothing to show |

### Capstone (week 12)
Lesson 48 contains a ten-criterion rubric with observable evidence for each criterion, the portfolio README template (checked by a
test), a 5-minute demo script (checked by a test) and an assessor's record. The decision rule is competency-based: **Competent only
if every criterion scores at least 2**; a percentage is separate if you need a grade. Keep one record per student:
name, date, the ten scores, what you saw, signature.

### Academic honesty
Students will search for answers; that is normal and useful. What matters is that they can **explain** their code. The oral
questions in the capstone rubric exist for that. `solution/` folders are public: tell students so on day 1 and ask them to use them
the way the lessons suggest (see the next section).

## 6. How to use the solutions
- `solution/` of each lesson is the **previous solution plus this lesson**, always green. Students can diff two neighbours
  (`lessons/37-.../solution` against `lessons/38-.../solution`) to see exactly how the application changed.
- **Students**: try first; read the failing test message; ask a neighbour; look at the solution **last**, then close it and write
  the code again from memory. Copying without understanding shows at the next lesson, because the next starter builds on it.
- **Instructors**: use `solution/` as the demo. Run it live, then break one thing on purpose and let the tests find it.
- **Checking a student's work** without trusting them: copy the lesson's tests over theirs (or ask them to run `./mvnw test` and show the output); see the table in section 7 for common failures.
- **Do not hand out the solutions as a file** if you want to grade a lesson; the repository is public, so the honest approach is to grade the explanation, not the code.
- The instructor can verify that a starter is solvable by overlaying the solution; CI proves that every solution passes.

## 7. Common student blockers

| Blocker | Typical cause | What to do |
|---|---|---|
| "Maven cannot be found" / `mvnw: Permission denied` | Wrong folder, or the executable bit lost | Run from the repository root; on Linux/macOS `chmod +x mvnw`; Windows: `mvnw.cmd` |
| `release version 21 not supported` | Older JDK on the path | `java -version` and `JAVA_HOME` must both say 21 |
| First build takes ages / fails to download | Slow or filtered network | Offline kit (section 4); check proxy settings in `~/.m2/settings.xml` |
| "My test fails and I do not know why" | Not reading the assertion message | Teach: read the first line (`expected ... but was ...`), then the first stack line that is *their* file |
| Starter tests fail from the beginning | They are meant to | Say it on day 1; the TODO comments say where to start |
| `Port 8080 already in use` | A previous run still alive | Stop it (Ctrl+C), or use another port |
| Changes to the code are not visible | They stopped and restarted the wrong module, or run an old jar | `./mvnw -pl <module> verify`, then run the jar from `target/` of that module |
| `401` / `403` confusion (weeks 11-12) | Mixing authentication and authorization | The table in lesson 41; make them call the API as stranger, cashier, admin |
| YAML will not parse (CI) | Tabs or wrong indentation | Spaces only, 2 per level; run lesson 43's test |
| Git: detached HEAD, merge conflict, committed a secret | Normal beginner events | Lesson 23; for a secret: change the secret first, clean the history second |
| Docker will not install (Windows Home, old CPU, BIOS virtualisation off) | Hardware or settings | Skip it: the lesson says which parts are optional; assess by explanation |
| Copy-pasting code that "looks right" with curly quotes | Word processors replace quotes | Type in the editor, or paste into a plain-text editor first |
| `curl` in PowerShell behaves differently | `curl` is an alias of `Invoke-WebRequest` in Windows PowerShell 5.1 | Use `curl.exe` or Git Bash (SETUP.md explains) |
| Fear of the terminal | Never used one | Ten minutes of `cd`, `ls`/`dir`, tab completion in week 1 pays back all course |

## 8. Keeping the repository healthy (for the person who maintains the course)
- `java scripts/LessonTool.java check` verifies every lesson README has its front matter and all eight numbered sections.
- `java scripts/LessonTool.java index` regenerates `lessons/index.json`; CI fails if the committed file is stale.
- `./mvnw -B verify`, `./mvnw -B -Pstudent test-compile`, `bash scripts/run-examples.sh` (or `scripts\run-examples.ps1`) are the three promises
  of the course; see [CONTRIBUTING.md](../CONTRIBUTING.md).
- The website contract for `index.json` is in [WEBSITE-INTEGRATION.md](WEBSITE-INTEGRATION.md).
