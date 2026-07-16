# Spaced Repetition

The application uses a spaced repetition scheduler.

The scheduler is responsible for calculating the next review date based on previous review performance.

The project should be designed so the scheduling algorithm can be replaced without affecting the rest of the application.

Example interface

```java
public interface ReviewScheduler {

    LocalDate calculateNextReview(
        ReviewResult result,
        ReviewHistory history
    );

}
```

Possible implementations

- Fixed intervals
- SM-2
- FSRS

The algorithm implementation should remain isolated from the domain.