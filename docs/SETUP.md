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

## Run a database example (lessons 25-32 and 38)
These examples talk to the free H2 database, which is one `.jar` file. Maven downloads it into your `~/.m2` folder the first time
you build (or fetch only the jar with `./mvnw -q dependency:get -Dartifact=com.h2database:h2:2.4.240`). Then:
```
java -cp ~/.m2/repository/com/h2database/h2/2.4.240/h2-2.4.240.jar lessons/26-sql-basics/examples/SqlTour.java
```
In PowerShell the jar is `$env:USERPROFILE\.m2\repository\com\h2database\h2\2.4.240\h2-2.4.240.jar`. To run every example of the course at once, use
`bash scripts/run-examples.sh`: it finds or downloads the jar for you (and, for lesson 35, the Jackson jars). No database server is installed.

## Run a web API and call it (lessons 33-40)
From lesson 33 a program stays running and waits for requests (stop it with `Ctrl+C`), and you call it from a **second terminal**. Maven downloads
the libraries (Jackson, Spring Boot, ...) the first time you build; nothing is installed. Each lesson says which command starts its server, for example:
```
./mvnw -B -q -pl lessons/40-testing-with-mockmvc/solution package -DskipTests
java -jar lessons/40-testing-with-mockmvc/solution/target/shop-api.jar
```
The database goes to a folder `shop-data` next to where you start the program (Git ignores it). Delete the folder to start again with an empty shop.

To call it you have three free choices:
1. **A browser** for every `GET`: open `http://localhost:8080/products`. (A browser cannot send a `POST` without a web page, so use one of the next two for those.)
2. **`curl`**, a command-line client. It comes with Git for Windows (open **Git Bash**), with macOS and with most Linux. Windows 10 and 11 also have `curl.exe`.
   In Git Bash, macOS and Linux:
   ```
   curl http://localhost:8080/products
   curl -i -X POST -H "Content-Type: application/json" -d '{"name":"Salt 1kg","price":20.00,"reorderLevel":5}' http://localhost:8080/products
   ```
   `-i` shows the status line and headers, `-X` is the method, `-H` a header, `-d` the body. Put the URL in quotes when it has `&` or `?`.
3. **PowerShell** (Windows). Two things differ:
   - In Windows PowerShell, `curl` is a different command (an alias of `Invoke-WebRequest`). Type **`curl.exe`**.
   - Windows PowerShell 5.1 loses the double quotes inside single-quoted JSON when it starts `curl.exe`. Put the JSON in a file and send the file (this works in PowerShell 7 too):
     ```
     Set-Content -Path body.json -Value '{"name":"Salt 1kg","price":20.00,"reorderLevel":5}'
     curl.exe -i -X POST -H "Content-Type: application/json" -d "@body.json" http://localhost:8080/products
     ```
   - Or use PowerShell's own client, which has no quoting problem (it turns the JSON answer into an object, and numbers lose their trailing zeros):
     ```
     Invoke-RestMethod http://localhost:8080/products
     Invoke-RestMethod -Method Post -Uri http://localhost:8080/products -ContentType "application/json" -Body '{"name":"Salt 1kg","price":20.00,"reorderLevel":5}'
     ```
   An error status (`404`, `409` ...) makes `Invoke-RestMethod` throw; the JSON of the error is in `$_.ErrorDetails.Message`.

Port 8080 busy ("Address already in use")? Another copy of the program is probably still running: stop it, or start with `--server.port=9090` (Spring Boot lessons)
or the port as the first argument (lessons 33 and 35).

## Work on a lesson
Open `lessons/NN-slug/starter` in your editor, follow the lesson `README.md`, then run:
```
cd lessons/NN-slug/starter
../../../mvnw test          # Windows:  ..\..\..\mvnw.cmd test
```
Red tests are normal at the start. Make them green. Peek at `solution/` only after trying.
