package fpJava.ch13_io;

import io.vavr.control.Try;

public interface Input {
    Try<ValueAndInput<String>> readString();

    Try<ValueAndInput<String>> readString(String message);

    Try<ValueAndInput<Integer>> readInt();

    Try<ValueAndInput<Integer>> readInt(String message);

    public static void main(String[] args) {

    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

    // impl AND type alias for Tuple<T, Input>
    record ValueAndInput<T>(T value, Input input) {}

    default <T> ValueAndInput<T> valueAndInput(T t) {
        return new ValueAndInput<>(t, this);
    }
}
