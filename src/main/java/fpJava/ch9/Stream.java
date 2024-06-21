package fpJava.ch9;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public sealed interface Stream<A> {
    static <A> Stream<A> empty() {return (Stream<A>) Empty.Instance;}

    static <A> Stream<A> cons(Supplier<A> head, Supplier<Stream<A>> tail) {
        return new Cons<>(head, tail);
    }

    A head();

    Stream<A> tail();

    boolean isEmpty();

    enum Empty implements Stream<Object> {
        Instance;

        @Override
        public Object head() {throw new NoSuchElementException("head of empty stream");}

        @Override
        public Stream<Object> tail() {throw new NoSuchElementException("tail of an empty stream");}

        @Override
        public boolean isEmpty() {return true;}
    }

    record Cons<A>(Supplier<A> hd, Supplier<Stream<A>> tl) implements Stream<A> {

        @Override
        public A head() {return hd.get();}

        @Override
        public Stream<A> tail() {return tl.get();}

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
