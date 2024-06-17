package fpJava.ch4;

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
        if (n < 3) return 1;
        return _recur(n - 2, 1, 1);
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

    public static void main(String[] args) {
        for (int i = 1; i < 10; i++) {
            System.out.println(loop(i) + "  " + recur(i) + "  " + heapifyRecur(i).eval());
        }

        for (int i = 0; i < 80; i++) {
            long expected = loop(i);
            assertThat(recur(i) == expected, "fib mismatch at: " + i);
            assertThat(heapifyRecur(i).eval() == expected, "fib mismatch at: " + i);
        }
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }
}
