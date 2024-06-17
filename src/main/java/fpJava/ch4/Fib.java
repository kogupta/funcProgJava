package fpJava.ch4;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Fib {
    private Fib() {}

    public static long loop(int n) {
        if (n < 3) return 1;

        long a = 1, b = 1, c;
        for (int i = 2; i < n; i++) {
            c = a + b;
            a = b;
            b = c;
        }

        return b;
    }

    public static long recur(int n) {
        return n < 3 ? 1 : _recur(n - 2, 1, 1);
    }

    private static long _recur(int n, long prev, long acc) {
        if (n == 0) return acc;

        return _recur(n - 1, acc, acc + prev);
    }

    public static TailCall<Long> heapifyRecur(int n) {
        // recur method type: int -> long
        // helper: int -> long -> long -> long
        return n < 3 ?
                TailCall.value(1L) :
                TailCall.suspend(() -> _heapifyRecur(n - 2, 1, 1));
    }

    private static TailCall<Long> _heapifyRecur(int n, long prev, long acc) {
        return n == 0 ?
                TailCall.value(acc) :
                TailCall.suspend(() -> _heapifyRecur(n - 1, acc, acc + prev));
    }

    private static <T> List<T> iterate(T seed, Function<T, T> generator, int count) {
        List<T> result = new ArrayList<>(count);
        T t = seed;
        for (int i = 0; i < count; i++) {
            result.add(t);
            t = generator.apply(t);
        }

        return result;
    }

    private static <T> String mkString(List<T> xs, String separator) {
        return _mkString(new StringBuilder(), xs, separator);
    }

    private static <T> String _mkString(StringBuilder acc, List<T> xs, String separator) {
        if (xs.isEmpty()) return acc.toString();
        acc.append(xs.getFirst());
        if (xs.size() > 1)
            acc.append(separator);
        return _mkString(acc, xs.subList(1, xs.size()), separator);
    }

    public static String coRecursive(int n) {
        Pair seed = new Pair(1, 1);
        List<Pair> pairs = iterate(seed, Pair::next, n);
        List<Long> fibs = pairs.stream().map(Pair::first).toList();
        return mkString(fibs, ", ");
    }

    public static void main(String[] args) {
        for (int i = 1; i < 10; i++) {
            System.out.println(loop(i) + "  " + recur(i) + "  " + heapifyRecur(i).eval());
        }

        for (int i = 0; i < 80; i++) {
            long expected = loop(i);
            assertThat(recur(i) == expected, "fib mismatch at: " + i);
            assertThat(heapifyRecur(i).eval() == expected, "fib mismatch at: " + i);
        }

        assertThat(coRecursive(10).equals("1, 1, 2, 3, 5, 8, 13, 21, 34, 55"), "...");
    }

    record Pair(long first, long second) {
        Pair next() {
            return new Pair(second, first + second);
        }
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }
}
