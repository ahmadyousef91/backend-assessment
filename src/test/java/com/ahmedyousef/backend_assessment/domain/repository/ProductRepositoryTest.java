package com.ahmedyousef.backend_assessment.domain.repository;

import com.ahmedyousef.backend_assessment.domain.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByDeletedFalse_shouldReturnOnlyActiveProducts() {
        Product p1 = productRepository.save(Product.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .price(new BigDecimal("4500.00"))
                .quantity(5)
                .build());

        Product p2 = productRepository.save(Product.builder()
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("100.00"))
                .quantity(50)
                .build());

        p2.softDelete();
        productRepository.save(p2);

        assertThat(productRepository.findByDeletedFalse())
                .extracting(Product::getId)
                .containsExactly(p1.getId());

        assertThat(productRepository.findByIdAndDeletedFalse(p1.getId())).isPresent();
        assertThat(productRepository.findByIdAndDeletedFalse(p2.getId())).isEmpty();
    }
}
