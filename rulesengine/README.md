# Rules Engine

A small Java rules engine that loads rules from a text file, matches them against a typed context, and runs registered actions.

Conditions and actions are registered in code. The file only names those identifiers; it does not define new Java behavior. There is no HTTP API, no persistence, and no audit log of why a rule fired.

## Rule file

```
RULE rule_2
WHEN condition_one
AND condition_two
THEN sum_one
THEN mult_two
```

- Start a rule with `RULE <id>`
- Conditions: `WHEN`, then `AND` / `OR` (one condition per line)
- Actions: one `THEN` per line

Every matching rule runs, not only the first one. `NOT` exists as a class but is not parsed from the file.

`Main` loads a sample file under `domain/test_rules/` and evaluates a numeric context.

## Stack

- Java 17
- Maven
- JUnit 4

## Run

```bash
mvn test
mvn exec:java -Dexec.mainClass=com.projects.Main
```
