// Run with:  java ObjectsTogetherDemo.java
import java.util.ArrayList;
import java.util.List;

public class ObjectsTogetherDemo {

    public static void main(String[] args) {
        Buyer ana = new Buyer("Ana Reyes");

        Receipt receipt = new Receipt(1, ana);          // a Receipt holds a Buyer ...
        receipt.add(new Line("Rice 25kg", 125050, 2));  // ... and a list of Line objects
        receipt.add(new Line("Sardines 155g", 2400, 5));

        System.out.println("Receipt #" + receipt.number + " for " + receipt.buyer.name);
        for (Line line : receipt.lines) {
            System.out.println("  " + line.quantity + " x " + line.product + " = " + line.total());
        }
        System.out.println("Total centavos: " + receipt.total());

        ana.points += receipt.total() / 10000;          // objects can change each other
        System.out.println(ana.name + " now has " + ana.points + " points");
    }
}

class Buyer {
    String name;
    int points;

    Buyer(String name) {
        this.name = name;
    }
}

class Line {
    String product;
    int unitPrice;
    int quantity;

    Line(String product, int unitPrice, int quantity) {
        this.product = product;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    int total() {
        return unitPrice * quantity;
    }
}

class Receipt {
    int number;
    Buyer buyer;
    List<Line> lines = new ArrayList<>();

    Receipt(int number, Buyer buyer) {
        this.number = number;
        this.buyer = buyer;
    }

    void add(Line line) {
        lines.add(line);
    }

    int total() {
        int sum = 0;
        for (Line line : lines) {
            sum += line.total();
        }
        return sum;
    }
}
