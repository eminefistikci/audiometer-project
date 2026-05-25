package com.audiometer.functional;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Maybe<T> {

    private final T value;

    private Maybe(T value) {
        this.value = value;
    }

    public static <T> Maybe<T> of(T value) {
        return value == null ? empty() : new Maybe<>(value);
    }

    public static <T> Maybe<T> empty() {
        return new Maybe<>(null);
    }

    public boolean isPresent() {
        return value != null;
    }

    public T get() {
        if (!isPresent()) {
            throw new NoSuchElementException("Maybe is empty");
        }
        return value;
    }

    public T orElse(T fallback) {
        return isPresent() ? value : fallback;
    }

    public <R> Maybe<R> map(Function<T, R> mapper) {
        return isPresent() ? Maybe.of(mapper.apply(value)) : Maybe.empty();
    }

    public Maybe<T> filter(Predicate<T> predicate) {
        return isPresent() && predicate.test(value) ? this : Maybe.empty();
    }
}
