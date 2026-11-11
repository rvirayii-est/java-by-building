# Setup (free tools only)

You need three things. Lessons 1–3 need only the first.

1. **JDK 21** (free): install *Eclipse Temurin 21* or *Microsoft Build of OpenJDK 21*.
   Check: `java -version` → should say `21`.
2. **Git** (free): https://git-scm.com — check `git --version`.
3. **An editor**: VS Code (+ "Extension Pack for Java") or IntelliJ IDEA Community, or any text editor.

No Maven install needed: use the wrapper in this repo — `./mvnw` (macOS/Linux/Git Bash) or `mvnw.cmd` (Windows).

## Get the course
```
git clone https://github.com/rvirayii-est/java-by-building.git
cd java-by-building
```

## Run an example (single file, no build tool)
```
java lessons/01-hello-java/examples/HelloJava.java
```

## Run a database example (lessons 25-32)
These examples talk to the free H2 database, which is one `.jar` file. Maven downloads it into your `~/.m2` folder the first time
you build (or fetch only the jar with `./mvnw -q dependency:get -Dartifact=com.h2database:h2:2.4.240`). Then:
```
java -cp ~/.m2/repository/com/h2database/h2/2.4.240/h2-2.4.240.jar lessons/26-sql-basics/examples/SqlTour.java
```
In PowerShell the jar is `$env:USERPROFILE\.m2\repository\com\h2database\h2\2.4.240\h2-2.4.240.jar`. To run every example of the course at once, use
`bash scripts/run-examples.sh`: it finds or downloads the jar for you. No database server is installed.

## Work on a lesson
Open `lessons/NN-slug/starter` in your editor, follow the lesson `README.md`, then run:
```
cd lessons/NN-slug/starter
../../../mvnw test          # Windows:  ..\..\..\mvnw.cmd test
```
Red tests are normal at the start. Make them green. Peek at `solution/` only after trying.
