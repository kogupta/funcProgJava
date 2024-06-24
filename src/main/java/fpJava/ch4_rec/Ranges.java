package fpJava.ch4_rec;

import java.util.ArrayList;
import java.util.List;

public final class Ranges {
    private Ranges() {}

    public static List<Integer> closedRange(int start, int end) {
        return start >= end ?
                List.of() :
                _helper(new ArrayList<>(), start, end).eval();
    }

    private static TailCall<List<Integer>> _helper(List<Integer> acc, int start, int end) {
        return start > end ?
                TailCall.value(acc) :
                TailCall.suspend(() -> {
                    acc.add(start);
                    return _helper(acc, start + 1, end);
                });
    }

    public static void main(String[] args) {
        assertThat(closedRange(1, 5).equals(List.of(1, 2, 3, 4, 5)), "[1..5]");
        List<Integer> range = closedRange(0, 100_000);
        for (int i = 0; i <= 100_000; i++) {
            assertThat(range.contains(i), "...");
        }
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

}
