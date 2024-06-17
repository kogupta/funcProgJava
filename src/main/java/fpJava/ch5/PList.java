package fpJava.ch5;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public sealed interface PList<T> { // P -> persistent
    PList<?> empty = new Nil<>();

    static <A> PList<A> concat(PList<A> xs, PList<A> ys) {
        if (xs.isEmpty()) return ys;
        if (ys.isEmpty()) return xs;

        return xs.reverse().foldLeft(ys, PList::prepend);
    }

    @SuppressWarnings("unchecked")
    static <T> PList<T> of() {return (PList<T>) empty;}

    @SafeVarargs
    static <T> PList<T> of(T... xs) {
        PList<T> result = of();
        for (int i = xs.length - 1; i >= 0; i--) {
            T x = xs[i];
            result = new Cons<>(x, result);
        }
        return result;
    }

    T head();

    PList<T> tail();

    boolean isEmpty();

    default PList<T> prepend(T elem) {
        return new Cons<>(elem, this);
    }

    PList<T> drop(int n);

    PList<T> dropWhile(Predicate<T> predicate);

    PList<T> reverse();

    <U> PList<U> map(Function<? super T, ? extends U> mapper);

    <U> PList<U> flatMap(Function<? super T, PList<U>> mapper);

    <U> U foldLeft(U zero, BiFunction<U, ? super T, U> combiner);

    record Nil<T>() implements PList<T> {
        @Override
        public T head() {throw new NoSuchElementException("head of an empty list");}

        @Override
        public PList<T> tail() {throw new NoSuchElementException("tail of an empty list");}

        @Override
        public boolean isEmpty() {return true;}

        @Override
        public PList<T> drop(int n) {return this;}

        @Override
        public PList<T> dropWhile(Predicate<T> predicate) {return this;}
    }

    record Cons<T>(T head, PList<T> tail) implements PList<T> {
        @Override
        public boolean isEmpty() {return false;}

        @Override
        public PList<T> drop(int n) {
            if (n <= 0) return this;
            if (n == 1) return tail();

            PList<T> result = tail();
            for (int remaining = n - 1; remaining > 0 && !result.isEmpty(); remaining--) {
                result = result.tail();
            }

            return result;
        }

        @Override
        public PList<T> dropWhile(Predicate<T> predicate) {
            PList<T> result = this;
            while (!result.isEmpty() && predicate.test(result.head())) {
                result = result.tail();
            }

            return result;
        }
    }
}
