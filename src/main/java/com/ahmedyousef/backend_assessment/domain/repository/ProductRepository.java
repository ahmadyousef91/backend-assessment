package com.ahmedyousef.backend_assessment.domain.repository;

import com.ahmedyousef.backend_assessment.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByDeletedFalse();

    Optional<Product> findByIdAndDeletedFalse(Long id);

}