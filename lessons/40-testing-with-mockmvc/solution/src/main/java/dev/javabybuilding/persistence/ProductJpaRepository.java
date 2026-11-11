package dev.javabybuilding.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A Spring Data repository: you write ONLY this interface and Spring writes the class. {@code JpaRepository<ProductEntity, Integer>}
 * already gives save, findById, findAll, delete, count ... for the entity type and its id type. A method whose NAME follows the
 * pattern findBy + a field name is turned into a query: the words are the SQL.
 *
 * <p>Compare with lesson 29: JdbcProductRepository was 110 lines of SQL strings, PreparedStatements and ResultSets.
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {

    /** select ... from product where upper(name) = upper(?) : "findBy" + "Name" + "IgnoreCase". */
    Optional<ProductEntity> findByNameIgnoreCase(String name);

    /** select ... from product order by id asc : the order the products were added. */
    List<ProductEntity> findAllByOrderByIdAsc();
}
