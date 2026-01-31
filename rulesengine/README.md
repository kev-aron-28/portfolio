# Explainable Business Rules Engine in Java

A lightweight and deterministic business rules engine implemented in plain Java.  
The goal of this project is to demonstrate how complex business rules can be externalized from application code, evaluated sequentially, and explained in a transparent and testable way.

This engine is inspired by enterprise and legacy systems commonly found in banking, insurance, and ERP platforms.

---

## Goals

- Separate business rules from application logic
- Allow rules to be added or modified without changing core code
- Provide deterministic and predictable rule execution
- Make rule execution explainable and auditable
- Keep the implementation simple and dependency-free

## What is a Rules Engine?
So instead of hardcoding business logic like:

``` java
if (task.isOverdue() && task.getPriority() == Priority.HIGH) {
    task.addScore(10);
}
```

You can write an external file with the rules to be matched like this:

```
RULE: OVERDUE_HIGH_PRIORITY
WHEN overdue = true 
AND priority = HIGH
THEN ADD_SCORE 10
```

# Rules of the engine rule file:

## Rule Identifier
TO start a new rule you must write RULE <ruleId>

```
RULE <rulId>
```

## Conditions
Each line should have a condition with AND or OR and the conditions section starts with
the WHEN keyword

```
RULE <id>
WHEN <condition>
OR <condition>
AND <condition>
```

## Actions
And once you write the condition section comes the actions section that starts with the
THEN keyword, each action to perform must be on a separate THEN 

```
RULE <id>
WHEN <condition>
OR <condition>
AND <condition>
THEN <action>
THEN <action>
THEN <action>
```