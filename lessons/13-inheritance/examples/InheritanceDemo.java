// Run with:  java InheritanceDemo.java
public class InheritanceDemo {

    public static void main(String[] args) {
        Item sugar = new Item("Sugar 1kg");
        Perishable milk = new Perishable("Fresh milk", 3);   // a Perishable IS-A Item

        System.out.println(sugar.label());
        System.out.println(milk.label());          // the Perishable version runs

        Item shelfItem = milk;                      // an Item variable can hold a Perishable object
        System.out.println(shelfItem.label());      // still the Perishable version
        System.out.println(milk.name());            // inherited from Item: Perishable did not write it

        System.out.println(milk instanceof Item);       // true
        System.out.println(sugar instanceof Perishable); // false
    }
}

class Item {
    private final String name;

    Item(String name) {
        this.name = name;
    }

    String name() {
        return name;
    }

    String label() {
        return name;
    }
}

class Perishable extends Item {
    private final int daysLeft;

    Perishable(String name, int daysLeft) {
        super(name);               // build the Item part first
        this.daysLeft = daysLeft;
    }

    @Override
    String label() {
        return super.label() + " (expires in " + daysLeft + " days)";   // reuse the parent version
    }
}
