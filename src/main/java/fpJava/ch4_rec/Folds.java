package fpJava.ch4_rec;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Folds {
    private Folds() {}

    public static <T, U> U foldLeft(List<T> xs, U identity, BiFunction<U, T, U> combiner) {
        return xs.isEmpty() ?
                identity :
                foldLeft(
                        xs.subList(1, xs.size()), // xs.tail
                        combiner.apply(identity, xs.getFirst()), // combine acc, xs.head
                        combiner
                );
    }

    public static <T, U> U heapifyFoldLeft(List<T> xs, U identity, BiFunction<U, T, U> combiner) {
        return _heapifyFoldLeft(xs, identity, combiner).eval();
    }

    private static <T, U> TailCall<U> _heapifyFoldLeft(List<T> xs, U identity, BiFunction<U, T, U> combiner) {
        return xs.isEmpty() ?
                TailCall.value(identity) :
                TailCall.suspend(() -> _heapifyFoldLeft(
                        xs.subList(1, xs.size()),
                        combiner.apply(identity, xs.getFirst()),
                        combiner)
                );
    }

    public static <T> Function<T, T> composeAll(List<Function<T, T>> functions) {
        return x -> foldLeft(functions.reversed(), x, (t, fn) -> fn.apply(t));
    }

    public static <T> Function<T, T> andThenAll(List<Function<T, T>> functions) {
        return x -> foldLeft(functions, x, (t, fn) -> fn.apply(t));
    }

    public static void main(String[] args) {
        String abc = foldLeft(List.of("a", "b", "c"), "", String::concat);
        assertThat(abc.equals("abc"), "string concat");
        assertThat(heapifyFoldLeft(List.of("a", "b", "c"), "", String::concat).equals(abc), "heaped");

        String oneTwoThree = foldLeft(List.of(1, 2, 3), "", (s, n) -> s + n);
        assertThat(oneTwoThree.equals("123"), "concat integers");
        assertThat(heapifyFoldLeft(List.of(1, 2, 3), "", (s, n) -> s + n).equals("123"), "concat integers");

        int sum = foldLeft(List.of(1, 2, 3, 4), 0, Integer::sum);
        assertThat(sum == 10, "sum");
        assertThat(heapifyFoldLeft(List.of(1, 2, 3, 4), 0, Integer::sum) == 10, "sum");

        Function<String, String> f1 = x -> "(a" + x + ")";
        Function<String, String> f2 = x -> "{b" + x + "}";
        Function<String, String> f3 = x -> "[c" + x + "]";
        List<Function<String, String>> list = List.of(f1, f2, f3);
        assertThat(composeAll(list).apply("x").equals("(a{b[cx]})"), "compose all - apply right to left");
        assertThat(andThenAll(list).apply("x").equals("[c{b(ax)}]"), "andThen all - apply left to right");
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

}
