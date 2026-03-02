package com.kosign.customer_crud.repository;

import com.kosign.customer_crud.dto.model.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {
}
