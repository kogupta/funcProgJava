package fpJava.ch12_state;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.function.Function;

// interface State<S, A> extends Function<S, Tuple2<S, A>>
public record State<S, A>(Function<S, Tuple2<S, A>> run) {
    public <B> State<S, B> map(Function<A, B> fn) {
        return new State<>(s -> {
            Tuple2<S, A> sa = run.apply(s);
            return Tuple.of(sa._1, fn.apply(sa._2));
        });
    }

    public <B> State<S, B> mapViaFlatMap(Function<A, B> fn) {
        return flatMap(a -> unit(fn.apply(a)));
    }

    public <B> State<S, B> flatMap(Function<A, State<S, B>> fn) {
        return new State<>(s -> {
            Tuple2<S, A> sa = run.apply(s);
            State<S, B> sb = fn.apply(sa._2);
            return sb.run().apply(sa._1);
        });
    }

    public <B, C> State<S, C> map2(State<S, B> sb, Function<A, Function<B, C>> fn) {
        return flatMap(a -> sb.map(b -> fn.apply(a).apply(b)));
    }

    public static <S, A> State<S, List<A>> sequence(List<State<S, A>> fs) {
        return fs.foldRight(
                unit(List.empty()),
                (f, acc) -> f.map2(acc, a -> as -> as.prepend(a))
        );
    }

    public static <S, A> State<S, A> unit(A a) {
        return new State<>(state -> Tuple.of(state, a));
    }
}
