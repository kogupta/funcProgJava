package fpJava.ch7_try;

@FunctionalInterface
public interface Effect<T> {
    void apply(T t);
}
