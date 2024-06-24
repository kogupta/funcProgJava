package fpJava.ch13.randomBlogpost;

import java.util.function.Function;
import java.util.function.Supplier;

// source: https://www.cultured.systems/2022/10/01/Implementing-IO-Java/
public sealed interface IO<T> {
    static <T> IO<T> pure(T t) {
        return new Pure<>(t);
    }

    static <T> IO<T> suspend(Supplier<T> action) {
        return new Suspend<>(action);
    }

    default <U> IO<U> flatMap(Function<T, IO<U>> f) {
        return new FlatMap<>(this, f);
    }

    default T run() {
        return switch (this){
            case Pure(T pure) -> pure;
            case Suspend(Supplier<T> action) -> action.get();
            case FlatMap<?, T> f -> flattenFlatMap(f).run();  // not stack safe
        };
    }

    private static <S, T> IO<T> flattenFlatMap(FlatMap<S, T> flatMap) {
        IO<S> io = flatMap.previous();
        S s = io.run();
        return flatMap.fn().apply(s);
    }

    public static void main(String[] args) {
        IO<Integer> io = IO.pure(0);
        for (int i = 0; i < 100; i++) {
            io = io.flatMap(n -> IO.pure(n + 1));
        }

        assertThat(io.run() == 100, "passing integers through flatMap");
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

}

record Pure<T>(T value) implements IO<T> {}
record FlatMap<S, T>(IO<S> previous, Function<S, IO<T>> fn) implements IO<T> {}
record Suspend<T>(Supplier<T> action) implements IO<T> {}