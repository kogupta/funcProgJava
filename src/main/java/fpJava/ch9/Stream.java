package fpJava.ch9;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

public sealed interface Stream<A> permits Stream.Cons, Stream.Empty {
    static <A> Stream<A> cons(Supplier<A> head, Supplier<Stream<A>> tail) {return new Cons<>(head, tail);}

    static <A> Stream<A> cons(A hd, Stream<A> tl) {return new Cons<>(hd, tl);}

    A head();

    static <A> Stream<A> empty() {return (Stream<A>) Empty.Instance;}

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

    final class Cons<A> implements Stream<A> {
        private Supplier<A> hd;
        private Supplier<Stream<A>> tl;

        private A head;
        private Stream<A> tail;

        private Cons(Supplier<A> hd, Supplier<Stream<A>> tl) {
            this.hd = hd;
            this.tl = tl;
        }

        private Cons(A hd, Stream<A> tl) {
            this.head = hd;
            this.tail = tl;
        }

        @Override
        public A head() {
            if (head == null) head = hd.get();
            return head;
        }

        @Override
        public Stream<A> tail() {
            if (tail == null) tail = tl.get();
            return tail;
        }

        @Override
        public boolean isEmpty() {return false;}
    }
}
