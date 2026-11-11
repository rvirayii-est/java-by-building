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

    /** TODO 1: one line about this person: just the name. Subclasses will override it to say more. */
    public String describe() {
        return null;
    }

    /** TODO 1: printing a person prints describe(), so each kind of person prints itself in its own way. */
    @Override
    public String toString() {
        return null;
    }
}
