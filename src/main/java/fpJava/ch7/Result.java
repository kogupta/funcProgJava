package fpJava.ch7;

import fpJava.ch6.Maybe;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Result<V> {
    static <V> Result<V> failure(String message) {
        return new Failure<>(message);
    }

    static <V> Result<V> failure(Exception e) {
        return new Failure<V>(e);
    }

    static <V> Result<V> failure(RuntimeException e) {
        return new Failure<V>(e);
    }

    static <V> Result<V> success(V value) {
        return new Success<>(value);
    }

    static <V> Result<V> of(Supplier<V> fn) {
        try {
            return success(fn.get());
        } catch (Exception e) {
            return failure(e);
        }
    }

    private static <V, U> Result<U> _checkedFlatMap(V v, Function<V, Result<U>> f) {
        try {
            return f.apply(v);
        } catch (Exception e) {
            return failure(e);
        }
    }

    private static <V> Result<V> _checkedFilter(Predicate<V> predicate, Success<V> result) {
        try {
            return predicate.test(result.get) ? result : failure("Condition didn't match");
        } catch (Exception e) {
            return failure(e);
        }
    }

    V get();

    default boolean isSuccess() {
        return switch (this) {
            case Success<V> _ -> true;
            case Failure<V> _ -> false;
        };
    }

    default boolean isFailure() {return !isSuccess();}

    default V getOrElse(V defaultValue) {
        return switch (this) {
            case Success(var v) -> v;
            case Failure<V> _ -> defaultValue;
        };
    }

    default V getOrElse(final Supplier<V> defaultValue) {
        return switch (this) {
            case Success(V v) -> v;
            case Result.Failure<V> _ -> defaultValue.get();
        };
    }

    default <U> Result<U> map(Function<V, U> f) {
        return switch (this) {
            case Success(V v) -> of(() -> f.apply(v));
            case Failure<V> _ -> (Result<U>) this;
        };
    }

    default <U> Result<U> flatMap(Function<V, Result<U>> f) {
        return switch (this) {
            case Success(V v) -> _checkedFlatMap(v, f);
            case Failure<V> _ -> (Result<U>) this;
        };
    }

    default Result<V> orElse(Supplier<Result<V>> defaultValue) {
        return map(_ -> this).getOrElse(defaultValue);
    }

    default Maybe<V> toMaybe() {
        return switch (this) {
            case Success(V v) -> Maybe.some(v);
            case Failure<V> _ -> Maybe.none();
        };
    }

    default Result<V> filter(Predicate<V> predicate) {
        return switch (this) {
            case Success<V> r -> _checkedFilter(predicate, r);
            case Failure<V> _ -> this;
        };
    }

    default boolean exists(Predicate<V> predicate) {
//        return map(predicate::test).getOrElse(false);
        try {
            return predicate.test(get());
        } catch (Exception e) {
            return false;
        }
    }

    default Result<V> mapFailure(String errorMessage) {
        return switch (this) {
            case Success(V _) -> this;
            case Failure(RuntimeException re) -> failure(new IllegalStateException(errorMessage, re));
        };
    }

    record Success<V>(V get) implements Result<V> {}

    record Failure<V>(RuntimeException re) implements Result<V> {
        public Failure(String message) {
            this(new IllegalStateException(message));
        }

        public Failure(Exception e) {
            this(new IllegalStateException(e.getMessage(), e));
        }

        @Override
        public V get() {throw re;}
    }
}
