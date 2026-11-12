<!--
  PORTFOLIO README TEMPLATE (lesson 48). Copy this file to the README.md of YOUR capstone repository and replace everything in [square brackets].
  Delete these comment lines. Keep the ten "##" headings in this order: a test (PortfolioTemplateTest) and your instructor look for them.
  Write for a stranger: a recruiter or a developer who has 2 minutes. Short sentences. No secrets, no passwords, no private data.
-->

# [Project name]: [one line that says what it does, for whom]

[![CI](https://github.com/[your-user]/[your-repo]/actions/workflows/ci.yml/badge.svg)](https://github.com/[your-user]/[your-repo]/actions/workflows/ci.yml)

## What it is
[Two or three sentences. The problem (a small shop that tracks stock and sales on paper), who uses it (the owner, the cashier) and what the program does about it.
 Say which part is YOURS: "I built the loyalty-points feature on top of the course's Inventory & Sales API."]

## Features
- [Feature 1, from the user's point of view: "A cashier records a sale and the stock goes down"]
- [Feature 2: "The owner sees which products are OUT or LOW"]
- [Your own capstone feature: "Customers earn 1 point per 100 pesos; the cashier can redeem points"]

## Tech stack
Java 21, Spring Boot 3.5, Spring Data JPA, Flyway, H2 [or: PostgreSQL], Spring Security (HTTP Basic, roles ADMIN and CASHIER),
springdoc OpenAPI, Spring Boot Actuator, JUnit 5 and MockMvc, Maven, Docker, GitHub Actions.

## Run it
You need Java 21 [and, optionally, Docker].
```bash
git clone https://github.com/[your-user]/[your-repo].git
cd [your-repo]
./mvnw -B verify                      # build and run all the tests
java -jar [path]/target/shop-api.jar  # start on http://localhost:8080
```
Demo logins on a fresh install (**for learning only**): `admin` / `admin123` and `cashier` / `cashier123`.
With Docker: `[the exact docker compose command, and which file to copy to .env]`.

## API
Interactive documentation: `http://localhost:8080/swagger-ui.html` (the OpenAPI file is at `/v3/api-docs`).

| Method and path | Who | What |
|---|---|---|
| `GET /products` | admin, cashier | list products |
| `POST /sales` | admin, cashier | record a sale |
| `GET /reports/low-stock` | admin | products that need ordering |
| [`your new endpoint`] | [who] | [what] |

## Tests
[N] tests, all passing (`./mvnw verify`). [One sentence on what kinds: unit tests of the rules, MockMvc tests of the endpoints, an end-to-end scenario.]
CI runs them on every push: see the badge above.

## What I learned
- [A concrete thing: "A migration must work on a table that already has rows."]
- [A mistake you made and how you found it: "My first role rule shadowed the next one, because the first match wins."]

<!-- TODO 3: this template is MISSING three sections: Architecture (between Tests and Demo), Demo (before What I learned) and "What I would do next" (last). Add them with the exact headings. -->
