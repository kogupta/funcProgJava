package fpJava.ch13_io;

import fpJava.ch13_io.IO.Nothing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Console {
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private Console() {}

    public static IO<String> readLine(Nothing nothing) {
        return () -> {
            try {
                return br.readLine();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    public static IO<Nothing> printLine(Object o) {
        return () -> {
            System.out.println(o);
            return Nothing.instance;
        };
    }

    public static void main(String[] args) {
        IO<Nothing> program = Console.printLine("Enter your name: ")
                .flatMap(Console::readLine)
                .map(Console::buildMessage)
                .flatMap(Console::printLine)
                .flatMap(_ -> printLine("Si, food!"))
                .flatMap(_ -> printLine("Ah yes, sea food \uD83E\uDD14"));
        program.run();

        var repeat = IO.repeat(3, printLine("Hello! Who goes there!"));
        repeat.run();
    }

    private static String buildMessage(String s) {
        return "Hello, %s! Do you see food?".formatted(s);
    }
}
