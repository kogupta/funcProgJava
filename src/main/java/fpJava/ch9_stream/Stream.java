package fpJava.ch9_stream;

import fpJava.ch5_list.PList;
import fpJava.ch6_option.Maybe;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public sealed interface Stream<A> extends Iterable<A> {
    A head();
    Stream<A> tail();
    boolean isEmpty();

    default Maybe<A> headMaybe() {
        return switch (this) {
            case Stream.Cons<A> v -> Maybe.some(v.head());
            case Stream.Empty _ -> Maybe.none();
        };
    }

    default PList<A> toList() {
        if (isEmpty()) return PList.empty();

        PList<A> result = PList.empty();
        for (A a : this)
            result.prepend(a);
        return result.reverse();
    }

    default Stream<A> take(int n) {
        return n <= 0 ? empty() : cons(head(), () -> tail().take(n - 1));
    }

//    default Stream<A> drop(int n) {
//        Stream<A> result = this;
//        int count = 0;
//        for (Iterator<A> iterator = this.iterator(); count < n && iterator.hasNext(); count++) {
//            result = result.tail();
//        }
//
//        return result;
//    }

//    default Stream<A> takeWhile(Predicate<A> predicate) {}
//    default Stream<A> dropWhile(Predicate<A> predicate) {}

    static <A> Stream<A> empty() {return (Stream<A>) Empty.Instance;}
    static <A> Stream<A> cons(Supplier<A> head, Supplier<Stream<A>> tail) {return new Cons<>(head, tail);}
    static <A> Stream<A> cons(A hd, Stream<A> tl) {return new Cons<>(hd, tl);}
    static <A> Stream<A> cons(A hd, Supplier<Stream<A>> tl) {return new Cons<>(hd, tl);}

    enum Empty implements Stream<Object> {
        Instance;

        @Override
        public Object head() {throw new NoSuchElementException("head of empty stream");}

        @Override
        public Stream<Object> tail() {throw new NoSuchElementException("tail of an empty stream");}

        @Override
        public boolean isEmpty() {return true;}

        @Override
        public Iterator<Object> iterator() {
            return EmptyIterator.Instance;
        }
    }

    enum EmptyIterator implements Iterator<Object> {
        Instance;

        @Override
        public boolean hasNext() {return false;}

        @Override
        public Object next() {throw new NoSuchElementException("next of empty iterator");}
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

        private Cons(A hd, Supplier<Stream<A>> tl) {
            this.head = hd;
            this.tl = tl;
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

        @Override
        public Iterator<A> iterator() {
            return new ConsIterator<>(this);
        }
    }

    final class ConsIterator<A> implements Iterator<A> {
        private Stream<A> curr;

        public ConsIterator(Stream<A> curr) {this.curr = curr;}

        @Override
        public boolean hasNext() {return !curr.isEmpty();}

        @Override
        public A next() {
            A a = curr.head();
            curr = curr.tail();
            return a;
        }
    }
}
