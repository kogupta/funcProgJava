package fpJava.ch3;

import java.util.List;

public final class Store {
    private Store() {}

    public static void main(String[] args) {
        Product toothPaste = new Product("Tooth paste", 1.5, 0.5);
        Product toothBrush = new Product("Tooth brush", 3.5, 0.3);
        List<OrderLine> order = List.of(
                new OrderLine(toothPaste, 2),
                new OrderLine(toothBrush, 3)
        );
        double weight = Folds.foldLeft(order, 0.0, (x, y) -> x + y.weight());
        double price = Folds.foldLeft(order, 0.0, (x, y) -> x + y.amount());
        System.out.printf("Total price: %s%n", price);
        System.out.printf("Total weight: %s%n", weight);
    }

    private static void assertThat(boolean predicate, String msg) {
        if (!predicate)
            throw new AssertionError(msg);
    }

    record Product(String name, double price, double weight) {}

    record OrderLine(Product product, int count) {
        public double weight() {
            return product().weight() * count;
        }

        public double amount() {
            return product().price() * count;
        }
    }

}
