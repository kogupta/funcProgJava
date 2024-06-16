package fpJava.ch3;

import fpJava.ch3.Emails.Result.Failure;
import fpJava.ch3.Emails.Result.Success;

import java.util.regex.Pattern;

public final class Emails {
    private static final Pattern emailPattern =
            Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,5}$");

    private Emails() {}

    public static void main(String[] args) {
        emailHandler("");
        emailHandler(null);
        emailHandler("john.doe@acme.org");
        emailHandler("this.is@my.email");
    }

    private static void emailHandler(String email) {
        toExec(validateEmail(email)).run();
    }

    private static Result<String> validateEmail(String email) {
        if (email == null || email.isEmpty())
            return Result.failure("email must not be null/empty");
        return emailPattern.matcher(email).matches() ?
                Result.success(email) :
                Result.failure("email is not valid");
    }

    private static Executable toExec(Result<String> result) {
        return switch (result) {
            case Failure(var error) -> () -> System.err.println("Error: " + error);
            case Success(var s) -> () -> System.out.println("Email sent to: " + s);
        };
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

    sealed interface Result<T> {
        static <T> Result<T> success(T value) {return new Success<>(value);}

        static <T> Result<T> failure(String error) {return new Failure<>(error);}

        T value();

        record Success<T>(T value) implements Result<T> {}

        record Failure<T>(String error) implements Result<T> {
            @Override
            public T value() {
                throw new RuntimeException("No value present");
            }
        }
    }

    public interface Executable {
        void run();
    }
}
