package fpJava.ch13;

import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.io.Console;
import java.util.Scanner;

enum ConsoleReader implements Input{
    Instance;

    private final Scanner scanner;

    private ConsoleReader() {
        Console console = System.console();
        this.scanner = new Scanner(console.reader());
    }

    @Override
    public Try<ValueAndInput<String>> readString() {
        try {
            return Try.success(valueAndInput(scanner.nextLine()));
        } catch (Exception e) {
            return Try.failure(e);
        }
    }

    @Override
    public Try<ValueAndInput<String>> readString(String message) {
        System.out.println(message);
        return readString();
    }

    @Override
    public Try<ValueAndInput<Integer>> readInt() {
        try {
            return Try.success(valueAndInput(scanner.nextInt()));
        } catch (Exception e) {
            return Try.failure(e);
        }
    }

    @Override
    public Try<ValueAndInput<Integer>> readInt(String message) {
        System.out.println(message);
        return readInt();
    }

    public static void main(String[] args) {
        Input input = ConsoleReader.Instance;
        helloWorld(input);

        Stream<Person> people = Stream.unfoldRight(input, ConsoleReader::readPerson);
        people.forEach(System.out::println);
    }

    private static Option<Tuple2<? extends Person, ? extends Input>> readPerson(Input input) {
        return person(input).
                <Tuple2<? extends Person, ? extends Input>>map(x -> new Tuple2<>(x.value(), x.input()))
                .toOption();
    }

    private static Try<ValueAndInput<Person>> person(Input input) {
        return input.readInt("Enter ID:")
                .flatMap(id -> id.input().readString("Enter first name:")
                        .flatMap(fName -> fName.input().readString("Enter last name:")
                                .map(lName ->
                                        lName.input().valueAndInput(
                                                Person.create(id.value(), fName.value(), lName.value())))));
    }

    private static void helloWorld(Input input) {
        Try<String> name = input.readString("Enter your name: ").map(ValueAndInput::value);
        Try<String> greeting = name.map("Hello, %s"::formatted);
        greeting.forEach(System.out::println);
    }

    record Person(int id, String firstName, String lastName) {
        static Person create(int id, String firstName, String lastName) {
            return new Person(id, firstName, lastName);
        }
    }
}
