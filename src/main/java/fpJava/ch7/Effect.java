package fpJava.ch7;

@FunctionalInterface
public interface Effect<T> {
    void apply(T t);
}
