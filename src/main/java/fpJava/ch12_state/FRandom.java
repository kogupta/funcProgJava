package fpJava.ch12_state;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.function.Function;

public interface FRandom<A> extends Function<RNG, Tuple2<RNG, A>> {
    static <A> FRandom<A> unit(A a) {
        return rng -> Tuple.of(rng, a);
    }

    static <A, B> FRandom<B> map(FRandom<A> s, Function<A, B> f) {
        return rng -> {
            Tuple2<RNG, A> t = s.apply(rng);
            return Tuple.of(t._1, f.apply(t._2));
        };
    }

    static <A, B> FRandom<B> flatMap(FRandom<A> fa, Function<A, FRandom<B>> f) {
        return rng -> {
            Tuple2<RNG, A> t = fa.apply(rng);
            FRandom<B> fb = f.apply(t._2);
            return fb.apply(t._1);
        };
    }

    static <A, B, C> FRandom<C> map2(FRandom<A> ra, FRandom<B> rb, Function<A, Function<B, C>> f) {
        return rng -> {
            Tuple2<RNG, A> ta = ra.apply(rng);
            Tuple2<RNG, B> tb = rb.apply(ta._1);
            C c = f.apply(ta._2).apply(tb._2);
            return Tuple.of(tb._1, c);
        };
    }

    // map and map2 in terms of flatMap
    // flatMap and unit are primitives
    static <A, B> FRandom<B> mapViaFlatMap(FRandom<A> fa, Function<A, B> f) {
        return flatMap(fa, a -> unit(f.apply(a)));
    }

    static <A, B, C> FRandom<C> map2ViaFlatMap(FRandom<A> fa, FRandom<B> fb, Function<A, Function<B, C>> f) {
        return flatMap(fa, a -> flatMap(fb, b -> unit(f.apply(a).apply(b))));
    }

    static <A, B, C> FRandom<C> map2ViaFlatMap2(FRandom<A> fa, FRandom<B> fb, Function<A, Function<B, C>> f) {
        return flatMap(fa, a -> map(fb, b -> f.apply(a).apply(b)));
    }

    static <A> FRandom<List<A>> sequence(List<FRandom<A>> rs) {
        FRandom<List<A>> z = unit(List.empty());
        return rs.foldRight(z, (r, rxs) -> map2(r, rxs, a -> as -> as.prepend(a)));
    }

    static void main(String[] args) {
        FRandom<Integer> rInt = RNG::nextInt;
        FRandom<Boolean> randoBool = FRandom.map(rInt, n -> n % 2 == 0);
        FRandom<Double> doubleRando = FRandom.map(rInt, n -> n / 2.0);
    }
}
