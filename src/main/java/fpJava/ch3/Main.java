package fpJava.ch3;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        _assertionStatus();
    }

    private static void _assertionStatus() {
        String status = Main.class.desiredAssertionStatus() ? "enabled" : "disabled";
        System.out.println("assertions: " + status);
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }
}
