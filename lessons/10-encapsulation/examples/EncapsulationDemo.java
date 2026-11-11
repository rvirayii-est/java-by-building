// Run with:  java EncapsulationDemo.java
import java.util.ArrayList;
import java.util.List;

public class EncapsulationDemo {

    public static void main(String[] args) {
        Drawer drawer = new Drawer();
        System.out.println("deposit 500: " + drawer.deposit(500));
        System.out.println("withdraw 800: " + drawer.withdraw(800));   // refused, balance is only 500
        System.out.println("balance: " + drawer.getBalance());
        // drawer.balance = 1000000;   <- would NOT compile: balance is private

        Shelf shelf = new Shelf();
        shelf.add("Rice");
        shelf.add("Sugar");

        List<String> leaked = shelf.leakyItems();
        leaked.clear();                                  // the outsider wipes the shelf's own list!
        System.out.println("after leaky clear: " + shelf.count());

        shelf.add("Rice");
        List<String> copy = shelf.safeItems();
        System.out.println("copy: " + copy);
        shelf.add("Salt");                                // the copy does not change
        System.out.println("copy after add: " + copy + ", shelf has " + shelf.count());
    }
}

class Drawer {
    private int balance;    // only methods of Drawer can touch this

    int getBalance() {
        return balance;
    }

    boolean deposit(int amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }

    boolean withdraw(int amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }
}

class Shelf {
    private final List<String> items = new ArrayList<>();

    void add(String item) {
        items.add(item);
    }

    int count() {
        return items.size();
    }

    List<String> leakyItems() {
        return items;                 // hands out the real list: anyone can change it
    }

    List<String> safeItems() {
        return List.copyOf(items);    // hands out a copy: the real list stays private
    }
}
