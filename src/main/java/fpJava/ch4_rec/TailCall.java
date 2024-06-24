package fpJava.ch4_rec;

import java.util.function.Supplier;

public sealed interface TailCall<T> {
    TailCall<T> resume();
    T eval();
    boolean isSuspend();

    static <T> TailCall<T> value(T value) {
        return new Return<>(value);
    }

    static <T> TailCall<T> suspend(Supplier<TailCall<T>> fn) {
        return new Suspend<>(fn);
    }

    record Return<T>(T eval) implements TailCall<T> {
        @Override
        public TailCall<T> resume() {throw new IllegalStateException("Return has no resume");}

        @Override
        public boolean isSuspend() {return false;}
    }

    record Suspend<T>(Supplier<TailCall<T>> suspendedFn) implements TailCall<T> {
        @Override
        public TailCall<T> resume() {return suspendedFn().get();}

        @Override
        public T eval() {
            TailCall<T> tc = this;
            while (tc.isSuspend()) {
                tc = tc.resume();
            }
            return tc.eval();
        }

        @Override
        public boolean isSuspend() {return true;}
    }
}
