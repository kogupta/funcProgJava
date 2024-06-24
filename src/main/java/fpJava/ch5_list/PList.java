package fpJava.ch5_list;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public sealed interface PList<T> extends Iterable<T> { // P -> persistent
    PList<?> empty = new Nil<>();

    static <A> PList<A> concat(PList<A> xs, PList<A> ys) {
        if (xs.isEmpty()) return ys;
        if (ys.isEmpty()) return xs;

        return xs.reverse().foldLeft(ys, PList::prepend);
    }

    @SuppressWarnings("unchecked")
    static <T> PList<T> empty() {return (PList<T>) empty;}

    @SafeVarargs
    static <T> PList<T> of(T... xs) {
        PList<T> result = empty();
        for (int i = xs.length - 1; i >= 0; i--) {
            T x = xs[i];
            result = new Cons<>(x, result);
        }
        return result;
    }

    T head();

    PList<T> tail();

    boolean isEmpty();

    default PList<T> prepend(T elem) {return new Cons<>(elem, this);}

    PList<T> drop(int n);

    PList<T> dropWhile(Predicate<T> predicate);

    PList<T> reverse();

    PList<T> filter(Predicate<T> predicate);
    <U> PList<U> map(Function<? super T, ? extends U> mapper);

    <U> PList<U> flatMap(Function<? super T, PList<U>> mapper);

    <U> U foldLeft(U zero, BiFunction<U, ? super T, U> combiner);

    <U> U foldRight(U zero, BiFunction<? super T, U, U> combiner);

    void foreach(Consumer<T> consumer);

    String toString();

    enum NilIterator implements Iterator {
        Instance;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException("next of an empty list");
        }
    }

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

        @Override
        public PList<T> reverse() {return this;}

        @Override
        public PList<T> filter(Predicate<T> predicate) {return this;}

        @Override
        public <U> PList<U> map(Function<? super T, ? extends U> mapper) {return (PList<U>) this;}

        @Override
        public <U> PList<U> flatMap(Function<? super T, PList<U>> mapper) {return (PList<U>) this;}

        @Override
        public <U> U foldLeft(U zero, BiFunction<U, ? super T, U> combiner) {return zero;}

        @Override
        public <U> U foldRight(U zero, BiFunction<? super T, U, U> combiner) {return zero;}

        @Override
        public void foreach(Consumer<T> consumer) {}

        @Override
        public Iterator<T> iterator() {
            return NilIterator.Instance;
        }

        @Override
        public String toString() {return "Nil";}
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

        @Override
        public PList<T> reverse() {
            return tail.isEmpty() ?
                    this :
                    foldLeft(empty(), PList::prepend);
        }

        @Override
        public PList<T> filter(Predicate<T> predicate) {
            PList<T> xs = foldLeft(empty(), (acc, t) -> predicate.test(t) ? acc.prepend(t) : acc);
            return xs.reverse();
        }

        @Override
        public <U> PList<U> map(Function<? super T, ? extends U> mapper) {
            PList<U> xs = foldLeft(empty(), (acc, t) -> acc.prepend(mapper.apply(t)));
            return xs.reverse();
        }

        @Override
        public <U> PList<U> flatMap(Function<? super T, PList<U>> mapper) {
            return foldLeft(empty(), (acc, t) -> concat(acc, (mapper.apply(t))));
        }

        @Override
        public <U> U foldLeft(U zero, BiFunction<U, ? super T, U> combiner) {
            U result = zero;
            for (T t : this)
                result = combiner.apply(result, t);

            return result;
        }

        @Override
        public <U> U foldRight(U zero, BiFunction<? super T, U, U> combiner) {
            return reverse().foldLeft(zero, (acc, t) -> combiner.apply(t, acc));
        }

        @Override
        public void foreach(Consumer<T> consumer) {
            for (T t : this)
                consumer.accept(t);
        }

        @Override
        public Iterator<T> iterator() {return new ConsIterator<>(this);}

        @Override
        public String toString() {
            BiFunction<StringBuilder, T, StringBuilder> combiner = (acc, t) ->
                    acc.isEmpty() ? acc.append(t) : acc.append(", ").append(t);
            return foldLeft(new StringBuilder(), combiner).toString();
        }
    }

    final class ConsIterator<T> implements Iterator<T> {
        private PList<T> xs;

        public ConsIterator(Cons<T> cons) {this.xs = cons;}

        @Override
        public boolean hasNext() {
            return !xs.isEmpty();
        }

        @Override
        public T next() {
            T t = xs.head();
            xs = xs.tail();
            return t;
        }
    }
}
