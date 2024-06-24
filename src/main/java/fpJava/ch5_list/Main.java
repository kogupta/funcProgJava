package fpJava.ch5_list;

import java.util.Iterator;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        // creation
        assertThat(PList.of("a").head().equals("a"), "head");
        assertThat(PList.of("a", "b").head().equals("a"), "head");
        assertThat(PList.of("a", "b", "c").head().equals("a"), "head");
        assertThat(PList.of("a", "b", "c").tail().head().equals("b"), "tail.head");
        assertThat(PList.of("a", "b", "c").tail().tail().head().equals("c"), "tail.tail.head");

        // drop
        PList<Integer> _1To5 = PList.of(1, 2, 3, 4, 5);
        for (int i = 0; i < 5; i++) {
            assertThat(
                    _1To5.drop(i).head() == i + 1,
                    "drop: " + (i + 1)
            );
        }

        // drop while
        assertThat(_1To5.dropWhile(n -> n < 0).toString().equals("1, 2, 3, 4, 5"), "dropWhile n < 0");
        assertThat(_1To5.dropWhile(n -> n < 1).toString().equals("1, 2, 3, 4, 5"), "dropWhile n < 1");
        assertThat(_1To5.dropWhile(n -> n < 2).toString().equals("2, 3, 4, 5"), "dropWhile n < 2");
        assertThat(_1To5.dropWhile(n -> n < 3).toString().equals("3, 4, 5"), "dropWhile n < 3");
        assertThat(_1To5.dropWhile(n -> n < 4).toString().equals("4, 5"), "dropWhile n < 4");
        assertThat(_1To5.dropWhile(n -> n < 5).toString().equals("5"), "dropWhile n < 5");
        assertThat(_1To5.dropWhile(n -> n < 6).toString().equals("Nil"), "dropWhile n < 6");
        assertThat(_1To5.dropWhile(n -> n < 9).toString().equals("Nil"), "dropWhile n < 9");

        // iteration
        int i = 1;
        for (Iterator<Integer> iterator = _1To5.iterator(); iterator.hasNext(); i++)
            assertThat(iterator.next() == i, "index: " + i);
        assertThat(i == 6, "expected: 6, got: " + i);

        // fold
        assertThat(_1To5.foldLeft(0, Integer::sum) == 15, "sum of list");
        assertThat(_1To5.foldLeft(1, (a, b) -> a * b) == 120, "factorial 5");

        // map
        assertThat(_1To5.map(n -> n * 2).foldLeft(0, Integer::sum) == 30, "double of list sum");
        assertThat(_1To5.map(n -> n * 2).toString().equals("2, 4, 6, 8, 10"), "double of list sum");

        // flatMap
        assertThat(_1To5.flatMap(n -> PList.of(n, 2 * n, 3 * n)).toString()
                        .equals("1, 2, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12, 5, 10, 15"),
                "double of list sum");

        _1To5.foreach(System.out::println);
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

}
