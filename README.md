# World Cup Scoreboard (Coding Exercise)

This repository implements the **Football World Cup Score Board** as a **simple in-memory library**.

The scoreboard supports these operations:

1. **Start a game** (initial score `0 - 0`, captures home team + away team)
2. **Finish a game** (removes it from the scoreboard)
3. **Update score** (home score + away score)
4. **Get a summary** ordered by:
   - **total score** (descending)
   - if totals are equal: **most recently started game first**

---

## Project structure (two modules)

This is a Maven multi-module project:

- **`scoreboard-core`** (required deliverable)
   - Pure Java library (domain model + operations)
   - In-memory storage (collections)
   - Demo `main` class showing usage
   - Unit tests for the domain logic

- **`scoreboard-web`** (**bonus**)
   - Spring Boot + Thymeleaf UI
   - Uses `scoreboard-core` as a dependency
   - Exists only as a convenient way to interact with the library

> The exercise explicitly asks for a library, not a REST API/service.  
> The web module is intentionally kept separate to avoid mixing concerns.

---

## Design notes

### Core API (domain)
The core module exposes the scoreboard behavior through domain classes. Invalid operations are handled consistently:

- invalid input or invalid operation → **throws an exception**
- valid operation → updates in-memory state

This makes failures explicit and keeps the domain rules enforceable and testable.

### Ordering rule for summary
Summary is ordered by:
1. **total score** (home + away) descending
2. **start order** descending (most recently started first) when totals tie

This tie-break is based on *start time* (not “last updated”).

---

## Assumptions

- **A team can only play one live game at a time.**  
  Starting a new game with a team that is already playing is not allowed.
- **Home and away teams cannot be the same.**
- **Scores cannot be negative.**
- **Updating or finishing a non-existent (or already finished) game is not allowed** and results in an exception.
- **Team name normalization:** team names are trimmed and normalized so that equivalent names compare equal (see `Team` behavior in core).

---

## How to build & test
    bash mvn clean test
To build all modules:
    bash mvn clean install
---

## Run the library demo (core)

The core module includes a runnable demo class:

- `com.example.scoreboard.demo.WorldCupScoreBoardLibraryDemo`

Run it from IntelliJ using the gutter icon, or via Maven exec tooling if you prefer (not required).

The demo:
- starts games
- performs score updates
- prints summaries
- demonstrates expected failures (invalid operations)

---

## Run the bonus web UI (web)

Start the Spring Boot app from `scoreboard-web`:

- main class: `com.example.scoreboardweb.WorldCupScoreboardApplication`

From the root with Maven (build dependencies automatically):
    bash mvn -pl scoreboard-web -am spring-boot:run
Or from IntelliJ:
    Run -> Edit Configurations -> Spring Boot App -> Run
Or from your IDE.
Or from the command line:
    mvn spring-boot:run -pl scoreboard-web

Open:

- http://localhost:8080/

The UI lets you:
- start games
- update scores
- finish games
- view the current summary

---

## Notes for reviewers

- The **library implementation is in `scoreboard-core`**.
- The **web UI is a bonus adapter** and is intentionally separated into `scoreboard-web`.
- All state is in-memory; no database is used.