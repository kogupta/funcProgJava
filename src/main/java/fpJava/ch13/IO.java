package fpJava.ch13;

import java.util.function.Function;

public interface IO<A> {
    A run();

    default <B> IO<B> map(Function<A, B> f) {
        return () -> f.apply(run());
    }

    default <B> IO<B> flatMap(Function<A, IO<B>> f) {
        return () -> {
            A a = this.run();
            return f.apply(a).run();
        };
    }

//    default IO add(IO other) {
//        return () -> {
//            run();
//            other.run();
//        };
//    }

    // ---- utility methods ----
    static IO<Nothing> empty() {
        return () -> Nothing.instance;
    }

    static <A> IO<A> unit(A a) {
        return () -> a;
    }

    final class Nothing {
        public static final Nothing instance = new Nothing();

        private Nothing() {}
    }
}
