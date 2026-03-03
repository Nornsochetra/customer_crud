package com.kosign.customer_crud.repository;

import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import com.kosign.customer_crud.dto.model.CustomerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {

    Optional<CustomerModel> findByUsername(String username);

    @Query("""
    SELECT c FROM CustomerModel c
    WHERE
        (:search IS NULL OR :search = '' OR
            LOWER(c.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        AND (CAST(:types AS string) IS NULL OR c.types = :types)
        AND (CAST(:status AS string) IS NULL OR c.status = :status)
""")
    Page<CustomerModel> findAllWithFilters(
            @Param("search") String search,
            @Param("types") Types types,
            @Param("status") Status status,
            Pageable pageable
    );
}
