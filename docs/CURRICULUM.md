# Curriculum — 12 weeks, 240 hours

20 hours per week. **Status:** ✅ implemented · 🚧 planned. Numbering is `lessons/NN-slug`.
Milestones (M1–M7) are runnable snapshots of the Inventory & Sales System.

| Wk | Theme | Lessons | App milestone |
|----|-------|---------|---------------|
| 1 | Java foundations | 01 Hello, Java ✅ · 02 Variables & money ✅ · 03 Decisions & loops ✅ · 04 Methods & arrays 🚧 | **M1** Stock report + price calculator ✅ (end of lesson 03) |
| 2 | Strings, input, collections | 05 Strings & text · 06 Scanner & input validation · 07 ArrayList & HashMap · 08 Console menu | **M2** Interactive console app |
| 3 | OOP I | 09 Classes & objects · 10 Encapsulation · 11 Product & Customer classes · 12 Static vs instance | |
| 4 | OOP II | 13 Inheritance · 14 Interfaces & polymorphism · 15 Enums & records · 16 Exceptions | **M3** Domain model (Product, Sale, SaleLine, StockMovement) |
| 5 | Collections, streams, files | 17 Generics & collections · 18 Streams · 19 Files & CSV · 20 `BigDecimal` & `java.time` | **M4** App saves to files |
| 6 | Testing & professional practice | 21 JUnit & TDD · 22 Debugging · 23 Git & GitHub flow · 24 Logging & clean code | |
| 7 | Databases I | 25 Relational thinking & ERD · 26 SQL basics · 27 Joins & reports · 28 Schema design for the shop | |
| 8 | Databases II | 29 JDBC · 30 Repository pattern · 31 Transactions (a sale is all-or-nothing) · 32 Migrations | **M5** Database-backed app |
| 9 | HTTP & REST, Spring Boot intro | 33 How the web works · 34 REST design · 35 JSON · 36 Spring Boot "Hello" | |
| 10 | Spring Boot | 37 Controllers/services · 38 Spring Data JPA · 39 Validation & errors · 40 Testing with MockMvc | **M6** REST API |
| 11 | Security & quality | 41 Auth basics · 42 Roles (admin/cashier) · 43 CI pipeline · 44 API docs (OpenAPI) | |
| 12 | Ship it | 45 Docker · 46 Deploy · 47 Monitoring & health checks · 48 Capstone & portfolio | **M7** Deployed application |

Each lesson ≈ 5 hours (guided + lab); 48 lessons ≈ 240 h. Weeks 1, 6 and 12 include a review/assessment block.

## Week-by-week checkpoints (instructor view)
| Wk | Assessable outcome |
|----|--------------------|
| 1 | Student runs M1 and explains every line of the report code; all lesson tests green |
| 4 | Student models a new entity (e.g. Supplier) without help |
| 6 | Student fixes a failing test via TDD and submits a PR |
| 8 | Student explains a transaction and why a sale needs one |
| 10 | Student adds a new API endpoint with a test |
| 12 | Capstone demo: deployed app + README + 5-minute walkthrough |

## Current implementation status
Lessons 01–03 and milestone M1 are complete and CI-verified. Everything else is planned in [ROADMAP.md](../ROADMAP.md).
