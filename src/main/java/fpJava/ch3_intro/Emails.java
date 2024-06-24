package fpJava.ch3_intro;

import java.util.regex.Pattern;

public final class Emails {
    private static final Pattern emailPattern =
            Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,5}$");
    private static final Effect<String> onSuccess = email -> System.out.println("Email sent to: " + email);
    private static final Effect<String> onFailure = error -> System.err.println("Error: " + error);

    private Emails() {}

    public static void main(String[] args) {
        validateEmail("");
        validateEmail(null);
        validateEmail("john.doe@acme.org");
        validateEmail("this.is@my.email");
    }

    private static void validateEmail(String email) {
        Result<String> result = emailChecker(email);
        result.forEach(onSuccess, onFailure);
    }

    private static Result<String> emailChecker(String email) {
        if (email == null || email.isEmpty())
            return Result.failure("email must not be null/empty");
        return emailPattern.matcher(email).matches() ?
                Result.success(email) :
                Result.failure("email is not valid");
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

    public interface Effect<T> {
        void apply(T t);
    }
}
