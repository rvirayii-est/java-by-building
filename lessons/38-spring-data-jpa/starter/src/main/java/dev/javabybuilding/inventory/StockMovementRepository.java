package dev.javabybuilding.inventory;

import java.util.List;

/** Where the stock log is kept: every unit that came in or went out. */
public interface StockMovementRepository {

    /** Appends one movement to the log. The product it names must exist. */
    void add(StockMovement movement);

    /** The whole log, oldest first. */
    List<StockMovement> findAll();
}
