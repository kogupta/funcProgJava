package fpJava.ch7;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Either<T, U> {
    static <T, U> Either<T, U> left(T value) {
        return new Left<>(value);
    }

    static <T, U> Either<T, U> right(U value) {
        return new Right<>(value);
    }

    default <R> Either<T, R> map(Function<U, R> f) {
        return switch (this) {
            case Left(var v) -> left(v);
            case Right(var v) -> right(f.apply(v));
        };
    }

    default <R> Either<T, R> flatMap(Function<U, Either<T, R>> f) {
        return switch (this) {
            case Left(var v) -> left(v);
            case Right(var v) -> f.apply(v);
        };
    }

    default U getOrElse(Supplier<U> defaultValue) {
        return switch (this) {
            case Left(T _) -> defaultValue.get();
            case Right(U u) -> u;
        };
    }

    default Either<T, U> orElse(Supplier<Either<T, U>> defaultValue) {
        return map(_ -> this).getOrElse(defaultValue);
    }

    record Left<T, U>(T value) implements Either<T, U> {
        @Override
        public String toString() {
            return String.format("Left(%s)", value);
        }
    }

    record Right<T, U>(U value) implements Either<T, U> {
        @Override
        public String toString() {
            return String.format("Right(%s)", value);
        }
    }
}
