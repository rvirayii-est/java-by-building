package dev.javabybuilding.inventory;

/** A person who works in the shop. A Staff IS-A Person: it has a name and phone, plus a role. */
public class Staff extends Person {

    private final Role role; // an enum: only CASHIER or MANAGER are possible (lesson 13 used a String that could be misspelled)

    public Staff(String name, String phone, Role role) {
        super(name, phone); // run the Person constructor first
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    /** Only managers may cancel a completed sale. The rule now lives in the Role enum. */
    public boolean canVoidSales() {
        return role.canVoidSales();
    }

    /** For example "Mara Cruz (cashier)". */
    @Override
    public String describe() {
        return getName() + " (" + role.label() + ")";
    }
}
