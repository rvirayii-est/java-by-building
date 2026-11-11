package dev.javabybuilding.inventory;

/**
 * Anyone the shop deals with by name: the data and behaviour that Customer and Staff have in common.
 * Customer and Staff "extend" Person: they get everything here for free and add their own.
 */
public class Person {

    private final String name;
    private final String phone;

    public Person(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    /** One line about this person. Subclasses override it to say more. */
    public String describe() {
        return name;
    }

    /** Printing a person prints describe(), so each kind of person prints itself in its own way. */
    @Override
    public String toString() {
        return describe();
    }
}
