package dev.javabybuilding.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, Integer> {

    /**
     * All sales with their customer, lines and the product of each line, in ONE query ("join fetch"). Without it, JPA would
     * load each sale, then ask the database again for its lines, then again for each product: 1 + N + N queries (the "N+1 problem").
     * The query language here (JPQL) uses class and field names, not table and column names.
     */
    @Query("select distinct s from SaleEntity s left join fetch s.customer left join fetch s.lines l left join fetch l.product order by s.id")
    List<SaleEntity> findAllWithDetails();
}
