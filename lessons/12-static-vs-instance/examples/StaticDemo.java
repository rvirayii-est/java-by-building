// Run with:  java StaticDemo.java
public class StaticDemo {

    public static void main(String[] args) {   // main is static: it runs before any object exists
        // static members belong to the CLASS: call them with the class name.
        System.out.println("Square of 7: " + Maths.square(7));
        System.out.println("Max shelf size: " + Shelf.MAX_ITEMS);

        // instance members belong to ONE OBJECT: you need an object first.
        Shelf top = new Shelf("Top");
        Shelf bottom = new Shelf("Bottom");
        top.add(3);
        top.add(4);
        bottom.add(1);
        System.out.println(top.name + " has " + top.items);        // 7  (its own)
        System.out.println(bottom.name + " has " + bottom.items);  // 1  (its own)

        // a static variable is ONE copy shared by every object
        System.out.println("Shelves created: " + Shelf.created);
        // Shelf.items would NOT compile: "items" needs an object to belong to.
    }
}

class Maths {
    static int square(int n) {   // uses no object data, so it can be static
        return n * n;
    }
}

class Shelf {
    static final int MAX_ITEMS = 50;   // a constant: static + final
    static int created = 0;            // shared counter (careful: shared state makes tests order-dependent)

    String name;                        // one per Shelf
    int items;                          // one per Shelf

    Shelf(String name) {
        this.name = name;
        created++;
    }

    void add(int amount) {
        if (items + amount <= MAX_ITEMS) {
            items += amount;
        }
    }
}
