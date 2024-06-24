package fpJava.ch4_rec;

import java.util.List;
import java.util.function.Function;

public final class Misc {
    // previous add method as function: int -> int -> TC<Int>
    private static Function<Integer, Function<Integer, TailCall<Integer>>> add2 =
            a -> b -> a == 0 ?
                    TailCall.value(b) :
                    TailCall.suspend(() -> add(a - 1, b + 1));

    private Misc() {}

    public static void main(String[] args) {
        TailCall<Integer> n = add(10_000, 10_000);
        assertThat(n.eval() == 20_000, "get addition right!");
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

    private static int sum(int a, int b) {
        if (a == 0)
            return b;

        return sum(a - 1, b + 1);
    }

    private static int sum2(int a, int b) {
        while (a > 0) {
            a--;
            b++;
        }
        return b;
    }

    static Integer sum(List<Integer> list) {
        return sumTail(list, 0);
    }

    static Integer sumTail(List<Integer> list, int acc) {
        return list.isEmpty()
                ? acc
                : sumTail(list.subList(1, list.size()), acc + list.getFirst());
    }

    private static TailCall<Integer> add(int a, int b) {
        return a == 0 ?
                TailCall.value(b) :
                TailCall.suspend(() -> add(a - 1, b + 1));
    }
}
