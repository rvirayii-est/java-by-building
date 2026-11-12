package dev.javabybuilding.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Integer> {

    /** where name = ? and phone = ? : two fields joined with And. */
    Optional<CustomerEntity> findByNameAndPhone(String name, String phone);
}
