// Run with:  java ClassesDemo.java
// Several classes in one file are fine. The FIRST class is the one that runs.
public class ClassesDemo {

    public static void main(String[] args) {
        // One blueprint (the class), two separate objects.
        Item rice = new Item("Rice 25kg", 12);
        Item sugar = new Item("Sugar 1kg", 0);

        System.out.println(rice.describe());
        System.out.println(sugar.describe());

        rice.quantity = rice.quantity - 2;   // change ONE object's field with the dot
        sugar.restock(24);                   // or call a method on it
        System.out.println(rice.describe());
        System.out.println(sugar.describe());

        Item alias = rice;                   // not a copy: a second name for the SAME object
        alias.quantity = 100;
        System.out.println("rice now has " + rice.quantity);

        Item nothing = null;                 // a variable can point at no object at all
        System.out.println("nothing is null: " + (nothing == null));
    }
}

class Item {
    String name;
    int quantity;

    Item(String name, int quantity) {   // the constructor runs once for every "new Item(...)"
        this.name = name;
        this.quantity = quantity;
    }

    void restock(int amount) {
        quantity += amount;
    }

    String describe() {
        return name + ": " + quantity;
    }
}
