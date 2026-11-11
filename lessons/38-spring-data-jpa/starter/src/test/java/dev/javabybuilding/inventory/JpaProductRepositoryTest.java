package dev.javabybuilding.inventory;

import dev.javabybuilding.persistence.JpaProductRepository;
import dev.javabybuilding.persistence.ProductJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

/** The ProductRepository contract of lesson 30, run on JPA. */
@ShopDataJpaTest
class JpaProductRepositoryTest extends ProductRepositoryContract {

    @Autowired
    ProductJpaRepository productJpa;

    @Override
    ProductRepository create() {
        return new JpaProductRepository(productJpa);
    }
}
