package fpJava.ch6;

import fpJava.ch5.PList;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Maybe<A> {
    static <T> Maybe<T> none() {return (Maybe<T>) None.Instance;}

    static <T> Maybe<T> some(T value) {return new Some<>(value);}

    static <A, B> Function<Maybe<A>, Maybe<B>> lift(Function<A, B> f) {
        return aMaybe -> aMaybe.map(f);
    }

    static <A, B> Function<Maybe<A>, Maybe<B>> liftEx(Function<A, B> f) {
        return aMaybe -> {
            try {
                return aMaybe.map(f);
            } catch (Exception e) {
                return none();
            }
        };
    }

    static <A, B> Function<A, Maybe<B>> hlift(Function<A, B> f) {
        return a -> {
            try {
                return some(f.apply(a));
            } catch (Exception e) {
                return none();
            }
        };
    }

    static <A, B, C> Maybe<C> map2(Maybe<A> ma, Maybe<B> mb, Function<A, Function<B, C>> f) {
        return ma.flatMap(a -> mb.map(b -> f.apply(a).apply(b)));
    }

    static <A, B, C, D> Maybe<D> map3(Maybe<A> ma,
                                      Maybe<B> mb,
                                      Maybe<C> mc,
                                      Function<A, Function<B, Function<C, D>>> f) {
        return ma.flatMap(a ->
                mb.flatMap(b ->
                        mc.map(c -> f.apply(a).apply(b).apply(c))));
    }

    // sequence-traverse are pairs
    // one is a "primitive", other can be derived from that "primitive" function
    // sequence as "primitive", traverse as derived
    static <A> Maybe<PList<A>> sequence(PList<Maybe<A>> xs) {
        PList<A> ys = xs.foldRight(PList.empty(),
                (aMaybe, acc) -> aMaybe.map(acc::prepend).getOrElse(() -> acc));

        return some(ys);
    }

    static <A, B> Maybe<PList<B>> traverse(PList<A> xs, Function<A, Maybe<B>> f) {
        return sequence(xs.map(f));
    }

    // traverse as "primitive" - sequence as derived
    static <A> Maybe<PList<A>> sequence2(PList<Maybe<A>> xs) {
        return traverse2(xs, Function.identity());
    }

    static <A, B> Maybe<PList<B>> traverse2(PList<A> xs, Function<A, Maybe<B>> f) {
        PList<B> ys = xs.foldRight(PList.empty(),
                (a, acc) -> f.apply(a).map(acc::prepend).getOrElse(() -> acc));
        return some(ys);
    }

    A getOrThrow();

    boolean isEmpty();

    A getOrElse(Supplier<A> defaultValue);

    Maybe<A> filter(Predicate<A> predicate);

    <B> Maybe<B> map(Function<? super A, ? extends B> f);

    <B> Maybe<B> flatMap(Function<? super A, Maybe<B>> f);

    Maybe<A> orElse(Supplier<Maybe<A>> defaultValue);

    enum None implements Maybe<Object> {
        Instance;

        @Override
        public Object getOrThrow() {throw new NoSuchElementException("value of None");}

        @Override
        public boolean isEmpty() {return true;}

        @Override
        public Object getOrElse(Supplier<Object> defaultValue) {return defaultValue.get();}

        @Override
        public Maybe<Object> filter(Predicate<Object> predicate) {return this;}

        @Override
        public <B> Maybe<B> map(Function<? super Object, ? extends B> f) {return (Maybe<B>) this;}

        @Override
        public <B> Maybe<B> flatMap(Function<? super Object, Maybe<B>> f) {return (Maybe<B>) this;}

        @Override
        public Maybe<Object> orElse(Supplier<Maybe<Object>> defaultValue) {return defaultValue.get();}
    }

    record Some<T>(T getOrThrow) implements Maybe<T> {
        @Override
        public boolean isEmpty() {return false;}

        @Override
        public T getOrElse(Supplier<T> defaultValue) {return getOrThrow;}

        @Override
        public Maybe<T> filter(Predicate<T> predicate) {return predicate.test(getOrThrow) ? this : none();}

        @Override
        public <B> Maybe<B> map(Function<? super T, ? extends B> f) {return some(f.apply(getOrThrow));}

        @Override
        public <B> Maybe<B> flatMap(Function<? super T, Maybe<B>> f) {return f.apply(getOrThrow);}

        @Override
        public Maybe<T> orElse(Supplier<Maybe<T>> defaultValue) {return this;}
    }
}
