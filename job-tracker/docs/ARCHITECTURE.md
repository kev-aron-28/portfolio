# Architecture Overview

The system follows Clean Architecture principles.

## Layers

Presentation (Thymeleaf + Spring MVC)
↓
Application (Use Cases)
↓
Domain
↓
Infrastructure

## Rules

- Domain must not depend on frameworks
- Application contains use cases
- Infrastructure contains external integrations
- Presentation only handles input/output

## Key design principles

- Separation of concerns
- Dependency inversion
- Explicit boundaries
- Testability