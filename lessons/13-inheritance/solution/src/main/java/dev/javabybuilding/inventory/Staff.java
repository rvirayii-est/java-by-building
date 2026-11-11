package dev.javabybuilding.inventory;

/** A person who works in the shop. A Staff IS-A Person: it has a name and phone, plus a role. */
public class Staff extends Person {

    private final String role; // "cashier" or "manager". shortcut: a String can be misspelled; lesson 15 replaces it with an enum.

    public Staff(String name, String phone, String role) {
        super(name, phone); // run the Person constructor first
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    /** Only managers may cancel a completed sale. */
    public boolean canVoidSales() {
        return role.equals("manager");
    }

    /** For example "Mara Cruz (cashier)". */
    @Override
    public String describe() {
        return getName() + " (" + role + ")";
    }
}
