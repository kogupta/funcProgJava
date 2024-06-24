package fpJava.ch10_trees;

public final class Main {
    private Main() {}

    public static void main(String[] args) {

    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

}
