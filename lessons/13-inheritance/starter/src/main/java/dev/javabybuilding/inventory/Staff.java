package dev.javabybuilding.inventory;

/** A person who works in the shop. A Staff IS-A Person: it has a name and phone, plus a role. */
public class Staff extends Person {

    private String role; // "cashier" or "manager" (make it final once the constructor stores it)

    /** TODO 2: the super(...) call is done for you; also store the role in the field. */
    public Staff(String name, String phone, String role) {
        super(name, phone); // run the Person constructor first
    }

    /** TODO 2: return the role. */
    public String getRole() {
        return null;
    }

    /** TODO 2: only managers may cancel a completed sale: true when the role equals "manager". */
    public boolean canVoidSales() {
        return false;
    }

    /** TODO 2: override describe(): for example "Mara Cruz (cashier)". Use getName() (the field is private in Person). Add @Override. */
    public String describe() {
        return null;
    }
}
