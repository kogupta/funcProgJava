package fpJava.ch9_stream;

public final class Streams {
    private Streams() {}

    public static Stream<Integer> from(int i) {
        return Stream.cons(i, () -> from(i + 1));
    }
}
