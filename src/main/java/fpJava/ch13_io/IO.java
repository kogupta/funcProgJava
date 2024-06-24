package fpJava.ch13_io;

import fpJava.ch5_list.PList;
import io.vavr.collection.Stream;

import java.util.function.Function;
import java.util.function.Supplier;

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

    static <A, B, C> IO<C> map2(IO<A> ioa, IO<B> iob, Function<A, Function<B, C>> f) {
        // for {
        //   a <- ioa
        //   b <- iob
        // } yield f(a)(b)
        return ioa.flatMap(a -> iob.map(b -> f.apply(a).apply(b)));
    }

    static <A, B> IO<B> forever(IO<A> ioa) {
//        IO<IO<B>> iob = () -> forever(ioa);
//        return ioa.flatMap(_ -> iob.run());

//        IO<IO<B>> iob = unit(forever(ioa));
//        return ioa.flatMap(_ -> iob.run());

        Supplier<IO<B>> iob = () -> forever(ioa);
        return ioa.flatMap(_ -> iob.get());
    }

    static <A> IO<PList<A>> repeat(int n, IO<A> io) {
        Stream<IO<A>> ios = Stream.fill(n, io);
        IO<PList<A>> zero = unit(PList.empty());
        return ios.foldRight(zero, (aio, acc) -> acc.flatMap(ys -> aio.map(ys::prepend)));
    }

    static <A, B> IO<B> fold(Stream<A> s, B z, Function<B, Function<A, IO<B>>> f) {
        return s.isEmpty()
                ? unit(z)
                : f.apply(z).apply(s.head()).flatMap(b -> fold(s.tail(), b, f));
    }

    static <A, B> IO<B> as(IO<A> a, B b) {
        return a.map(_ -> b);
    }

    static <A> IO<Nothing> skip(IO<A> a) {
        return as(a, Nothing.instance);
    }

    static <A, B> IO<Nothing> fold_(Stream<A> s, B z, Function<B, Function<A, IO<B>>> f) {
        return skip(fold(s, z, f));
    }

    static <A> IO<Nothing> forEach(Stream<A> s, Function<A, IO<Nothing>> f) {
        return fold_(s, Nothing.instance, n -> a -> skip(f.apply(a)));
    }

    static <A> IO<Nothing> sequence(Stream<IO<A>> stream) {
        return forEach(stream, IO::skip);
    }

    @SafeVarargs
    static <A> IO<Nothing> sequence(IO<A>... array) {
        return sequence(Stream.of(array));
    }

    final class Nothing {
        public static final Nothing instance = new Nothing();

        private Nothing() {}
    }
}
