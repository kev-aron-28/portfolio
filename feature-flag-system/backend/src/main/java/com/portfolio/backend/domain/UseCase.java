package com.portfolio.backend.domain;

public interface UseCase<I, O> {
    O run(I input);
}
