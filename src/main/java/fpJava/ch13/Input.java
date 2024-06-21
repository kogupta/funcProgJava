package fpJava.ch13;

import io.vavr.control.Try;

public interface Input {
    Try<InputT<String>> readString();

    Try<InputT<String>> readString(String message);

    Try<InputT<Integer>> readInt();

    Try<InputT<Integer>> readInt(String message);

    public static void main(String[] args) {

    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

    // impl AND type alias for Tuple<T, Input>
    record InputT<T>(T t, Input input) {}
}
