package fpJava.ch10_trees;

import fpJava.ch6_option.Maybe;

import java.util.Objects;

public final class Main {
    public static void main(String[] args) {
        Tree<Integer> tree = Tree.tree(3, 1, 8, 0, 2, 6, 10, 5, 7, 9, 11);
        assertThat(tree.size() == 11, "should have 11 entries, got: " + tree.size());

        for (int i = 0; i < 11; i++) {
            if (i != 4)
                assertThat(tree.exists(i), "tree should contain: " + i);
        }
        assertThat(!tree.exists(4), "tree should NOT contain: 4");

        assertThat(tree.height() == 3, "has 3 segments in longest chain");

        assertThat(Objects.equals(tree.max(), Maybe.some(11)), "max value should be 11");
        assertThat(Objects.equals(tree.min(), Maybe.some(0)), "min value should be 0");
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

}
