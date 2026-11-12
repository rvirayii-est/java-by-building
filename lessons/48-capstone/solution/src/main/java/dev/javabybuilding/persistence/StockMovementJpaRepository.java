package dev.javabybuilding.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementEntity, Integer> {

    @Query("select m from StockMovementEntity m join fetch m.product order by m.id")
    List<StockMovementEntity> findAllWithProduct();
}
