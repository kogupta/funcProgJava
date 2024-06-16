package fpJava.ch3;

// an Either type
sealed interface Result<T> {
    static <T> Result<T> success(T value) {return new Success<>(value);}

    static <T> Result<T> failure(String error) {return new Failure<>(error);}

    T value();

    void forEach(Emails.Effect<T> success, Emails.Effect<String> failure);

    record Success<T>(T value) implements Result<T> {
        @Override
        public void forEach(Emails.Effect<T> success, Emails.Effect<String> failure) {
            success.apply(value);
        }
    }

    record Failure<T>(String error) implements Result<T> {

        @Override
        public T value() {
            throw new RuntimeException("No value present");
        }

        @Override
        public void forEach(Emails.Effect<T> success, Emails.Effect<String> failure) {
            failure.apply(error);
        }

    }
}
