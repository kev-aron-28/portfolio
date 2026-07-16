Follow these architectural rules.

- Controllers contain no business logic.
- Services contain all business rules.
- Repositories only access the database.
- DTOs are used between controllers and services.
- Entities must never be exposed directly.
- Use constructor injection.
- Keep methods short and cohesive.
- Prefer composition over inheritance.
- Follow SOLID principles.