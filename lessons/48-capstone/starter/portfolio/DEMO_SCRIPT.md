# The 5-minute demo script (lesson 48)

Rehearse it twice with a timer. Say the words in the quotes in your own style; do the actions exactly. Have the app already running, a terminal and a browser open, and your repository on screen.
TODO 4: these times add up to 5:30, not 5:00. Shorten sections so that each starts where the previous one ends and the last one ends at 5:00 (PortfolioTemplateTest checks it).

### 0:00 - 0:30 Say what it is
"Hello, I am [name]. This is the Inventory and Sales API I built in the course: a small shop's stock and sales, as a secured, documented, containerised REST API. My own addition is [your capstone feature]."
Show: the README top (title, the green CI badge).

### 0:30 - 1:15 Show that it is secured
Terminal: `curl -i localhost:8080/products` -> 401 in the JSON error shape.
"Nobody gets in without a password. Passwords are stored as BCrypt hashes."
Then: `curl -u cashier:cashier123 localhost:8080/products` -> 200.

### 1:15 - 2:15 Show the roles
As the cashier: record a sale (`POST /sales`) -> 201 with a Location header.
As the cashier: `curl -u cashier:cashier123 localhost:8080/reports/stock` -> 403.
"A cashier can sell but not read reports or change the catalogue. An admin can."

### 2:15 - 3:15 Show your feature
Do the one thing your capstone adds, from the user's point of view, with real requests. Show the answer, then the same request with a wrong input (a 400 with the field named).
"I added [feature]. It touches [layers]. Here it is failing correctly."

### 3:15 - 3:45 Show the documentation
Browser: `/swagger-ui.html`. Open your new endpoint. Click Authorize, sign in, Try it out.
"The documentation is generated from the code, and a test fails if I forget to document an endpoint."

### 3:45 - 4:15 Show that it is operated
`curl localhost:8080/actuator/health` -> UP. Show one log line with its request id.
"A platform can ask if it is alive and ready; every request has an id in the log."

### 4:15 - 4:45 Show the proof
Show the CI run (green) or `./mvnw verify` ending with BUILD SUCCESS and the number of tests.
"[N] tests pass on every push. Docker builds the same app anywhere." (Say honestly if you did not run Docker.)

### 4:45 - 5:30 Close
"What I learned: [one thing]. What I would do next: [one thing]. Thank you; questions?"

## If something breaks during the demo
Say what you see, read the error aloud, and continue with the next section. Evaluators score how you handle it. Never type a real password on screen.
