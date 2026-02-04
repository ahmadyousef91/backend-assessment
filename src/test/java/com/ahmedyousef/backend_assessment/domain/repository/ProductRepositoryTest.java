package com.ahmedyousef.backend_assessment.domain.repository;

import com.ahmedyousef.backend_assessment.domain.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

        var active = productRepository.findByDeletedFalse();

        assertThat(active).allMatch(p -> !p.isDeleted());
        assertThat(active).extracting(Product::getId)
                .contains(p1.getId())
                .doesNotContain(p2.getId());

        assertThat(productRepository.findByIdAndDeletedFalse(p1.getId())).isPresent();
        assertThat(productRepository.findByIdAndDeletedFalse(p2.getId())).isEmpty();
    }
}
