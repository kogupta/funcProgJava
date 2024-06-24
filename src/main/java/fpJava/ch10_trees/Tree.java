package fpJava.ch10_trees;

import fpJava.ch5_list.PList;
import fpJava.ch6_option.Maybe;

import java.util.NoSuchElementException;
import java.util.Objects;

public sealed interface Tree<A extends Comparable<A>> {
    A value();
    Tree<A> left();
    Tree<A> right();

    boolean isEmpty();

    int size();
    int height();

    Tree<A> insert(A a);

    default boolean exists(A a) {
        return switch (this) {
            case Empty<A> _ -> false;
            case Node<A> node -> {
                int c = a.compareTo(node.value());
                yield c == 0 ?
                              Objects.equals(a, node.value()) :
                              c > 0 ? node.right().exists(a) : node.left().exists(a);
            }
        };
    }

    default Maybe<A> max() {
        return switch (this) {
            case Tree.Empty<A> _ -> Maybe.none();
            case Tree.Node<A> node -> {
                Maybe<A> result = Maybe.some(node.value());
                Tree<A> curr = node;
                while (curr.right() != empty) {
                    result = Maybe.some(curr.right().value());
                    curr = curr.right();
                }
                yield result;
            }
        };
    }

    default Maybe<A> min() {
        return switch (this) {
            case Tree.Empty<A> _ -> Maybe.none();
            case Node(var left, A value, _, _, _) -> left.isEmpty() ?
                                              Maybe.some(value) :
                                              left().min();
        };
    }

    // forget deletion: imperative/(im)mutable/functional - deletion is HARD!
//    default Tree<A> remove(A a) {
//        return switch (this) {
//            case Empty<A> v -> empty();
//            case Node(var left, A value, var right, _, _) ->
//                    Objects.equals(a, value) ? _mergeChildren(left, right) :
//                    a.compareTo(value) > 0 ? right.remove(a) : left.remove(a);
//        };
//    }

    // factory methods
    @SuppressWarnings("rawtypes")
    Tree empty = new Empty();

    @SuppressWarnings("unchecked")
    static <A extends Comparable<A>> Tree<A> empty() {
        return (Tree<A>) empty;
    }

    static <A extends Comparable<A>> Tree<A> tree(PList<A> as) {
        return as.foldLeft(empty(), Tree::insert);
    }

    @SafeVarargs
    static <A extends Comparable<A>> Tree<A> tree(A... as) {
        return tree(PList.of(as));
    }

    // impl
    record Empty<A extends Comparable<A>>() implements Tree<A> {
        @Override
        public A value() {throw new NoSuchElementException("value of an empty tree");}

        @Override
        public Tree<A> left() {throw new NoSuchElementException("left of an empty tree");}

        @Override
        public Tree<A> right() {throw new NoSuchElementException("right of an empty tree");}

        @Override
        public boolean isEmpty() {return true;}

        @Override
        public int size() {return 0;}

        // height returns "segment" - NOT the number of nodes in longest chain
        @Override public int height() {return -1;}

        @Override
        public Tree<A> insert(A a) {return new Node<>(empty(), a, empty());}
    }

    record Node<A extends Comparable<A>>(Tree<A> left, A value, Tree<A> right, int size, int height)
            implements Tree<A> {
        public Node(Tree<A> left, A value, Tree<A> right) {
            this(left, value, right,
                    1 + left.size() + right.size(),
                    1 + Math.max(left.height(), right.height()));
        }

        @Override
        public Tree<A> insert(A a) {
            int n = a.compareTo(value);

            return n == 0 ? new Node<>(left, a, right)
                           : n > 0 ? new Node<>(left, value, right.insert(a)) :
                                     new Node<>(left.insert(a), value, right);
        }

        @Override
        public boolean isEmpty() {return false;}
    }

}
