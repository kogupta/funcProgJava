# funcProgJava
Working through [Functional Programming in Java](https://www.manning.com/books/functional-programming-in-java) book

![Func Prog Java from Manning Publications](BookCover.png "Func Prog Java from Manning Publications")

### Notes

#### Chapter 5 - persistent list

- `foldRight` => `foldLeft` followed by `reverse`
- [vavr List](https://github.com/vavr-io/vavr/blob/master/src/main/java/io/vavr/collection/List.java#L811)
- [scala immutable List](https://github.com/scala/scala/blob/2.13.x/src/library/scala/collection/immutable/List.scala)
- interesting persistent collections lib [bifurcan](https://github.com/lacuna/bifurcan/blob/master/doc/comparison.md)

#### Chapter 6 - Maybe

`traverse` and `sequence` are dual/inverse: one can be derived using the other as "primitive".

```java
// `sequence` as "primitive"
static <A, B> Maybe<PList<B>> traverse(PList<A> xs, Function<A, Maybe<B>> f) {
    return sequence(xs.map(f));
}

// conversely, with `traverse` as "primitive"
static <A> Maybe<PList<A>> sequence(PList<Maybe<A>> xs) {
    return traverse(xs, Function.identity());
}
```
