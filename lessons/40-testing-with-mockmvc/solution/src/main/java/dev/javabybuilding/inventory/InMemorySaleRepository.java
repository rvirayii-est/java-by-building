package dev.javabybuilding.inventory;

import java.util.ArrayList;
import java.util.List;

/** Sales kept in a list in memory (for fast tests). */
public class InMemorySaleRepository implements SaleRepository {

    private final List<Sale> sales = new ArrayList<>();

    @Override
    public Sale add(Sale draft) {
        Sale stored = draft.withNumber(sales.size() + 1);
        sales.add(stored);
        return stored;
    }

    @Override
    public List<Sale> findAll() {
        return List.copyOf(sales);
    }
}
