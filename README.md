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

#### Chapter 13: IO

- feels rushed 🙁
- much better coverage in [Grokking FP: IO](https://learning.oreilly.com/library/view/grokking-functional-programming/9781617291838/OEBPS/Text/08.html)


#### Conclusion

FP in Java is no fun at all - [Javaslang](https://github.com/vavr-io/vavr), `sealed traits + pattern matching` help 
a lot though. 

Biggest pain points: 
  - no `for-comprehension`/sugar for chained `flatMap`s
  - function application is - for lack of a better word - "Java"-ish; we have to ALWAYS mention `fn.apply()` - `fn()` would have been much nicer to read.
  - no local functions - again, readability hurts; all looping/helper functions with tail calls - they are almost always specific to a user facing api, no reusability whatsoever but defined at larger class scope rather than the restricted scope of usage (the method).