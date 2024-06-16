package fpJava.ch3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;

public final class Folds {
    private Folds() {}

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        assertThat(fold(list, 0, Integer::sum) == 15, "sum of 1..5 == 15");
        assertThat(fold(list, 1, (a, b) -> a * b) == 120, "product of 1..5 == 120");

        {
            assertThat(foldLeft(list, 0, Integer::sum) == 15, "sum of 1..5 == 15");
            assertThat(foldLeft(list, 1, (a, b) -> a * b) == 120, "product of 1..5 == 120");
            String got = foldLeft(list, "0", "(%s + %d)"::formatted);
            String expected = "(((((0 + 1) + 2) + 3) + 4) + 5)";
            assertThat(got.equals(expected), "foldLeft debug: got: " + got);
        }
        {
            assertThat(foldRight(list, 0, Integer::sum) == 15, "sum of 1..5 == 15");
            assertThat(foldRight(list, 1, (a, b) -> a * b) == 120, "product of 1..5 == 120");
            String got = foldRight(list, "0", "(%d + %s)"::formatted);
            String expected = "(1 + (2 + (3 + (4 + (5 + 0)))))";
            assertThat(got.equals(expected), "foldRight debug: got: " + got);
        }
        {
            List<Integer> got = mapViaFold(List.of(1, 2, 3), n -> n + 1);
            assertThat(got.equals(List.of(2, 3, 4)), "mapViaFold: got: " + got);
        }
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

    private static int fold(List<Integer> xs, int startingValue, IntBinaryOperator accumulator) {
        int result = startingValue;
        for (int x : xs) result = accumulator.applyAsInt(result, x);
        return result;
    }

    private static <T, R> R foldLeft(List<T> xs, R zero, BiFunction<R, T, R> combiner) {
        R result = zero;
        for (T x : xs) result = combiner.apply(result, x);
        return result;
    }

    private static <T, R> R foldRight(List<T> xs, R zero, BiFunction<T, R, R> combiner) {
        R result = zero;

        for (int i = xs.size() - 1; i >= 0; i--) {
            T x = xs.get(i);
            result = combiner.apply(x, result);
        }

        return result;
    }

    private static <T, R> List<R> mapViaFold(List<T> xs, Function<T, R> mapper) {
        List<R> result = new ArrayList<>(xs.size());
        return foldLeft(xs, result, (acc, t) -> {
            acc.add(mapper.apply(t));
            return acc;
        });
    }
}
