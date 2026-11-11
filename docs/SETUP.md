# Setup (free tools only)

You need three things. Lessons 1–3 need only the first.

1. **JDK 21** (free): install *Eclipse Temurin 21* or *Microsoft Build of OpenJDK 21*.
   Check: `java -version` → should say `21`.
2. **Git** (free): https://git-scm.com — check `git --version`.
3. **An editor**: VS Code (+ "Extension Pack for Java") or IntelliJ IDEA Community, or any text editor.

No Maven install needed: use the wrapper in this repo — `./mvnw` (macOS/Linux/Git Bash) or `mvnw.cmd` (Windows).

## Get the course
```
git clone https://github.com/OWNER/java-by-building.git
cd java-by-building
```

## Run an example (single file, no build tool)
```
java lessons/01-hello-java/examples/HelloJava.java
```

## Work on a lesson
Open `lessons/NN-slug/starter` in your editor, follow the lesson `README.md`, then run:
```
cd lessons/NN-slug/starter
../../../mvnw test          # Windows:  ..\..\..\mvnw.cmd test
```
Red tests are normal at the start. Make them green. Peek at `solution/` only after trying.
