# Domain Model

## Topic

Represents a learning category.

Fields

- id
- name
- description
- color

---

## Problem

Represents one algorithm problem.

Fields

- id
- title
- url
- difficulty
- description
- favorite
- archived
- createdAt

Relationships

- belongs to one Topic
- has one Solution
- has many Reviews
- has many Tags

---

## Solution

Stores the user's implementation.

Fields

- id
- language
- sourceCode
- explanation
- complexity
- mistakes

---

## Review

Represents one review session.

Fields

- id
- reviewDate
- rating
- notes
- nextReviewDate
- reviewDuration

---

## Tag

Represents algorithm patterns.

Examples

- BFS
- DFS
- Sliding Window
- Binary Search
- Two Pointers