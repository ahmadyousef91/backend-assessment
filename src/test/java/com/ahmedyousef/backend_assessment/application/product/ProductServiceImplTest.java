package com.ahmedyousef.backend_assessment.application.product;

import com.ahmedyousef.backend_assessment.api.dto.ProductRequest;
import com.ahmedyousef.backend_assessment.api.dto.ProductResponse;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void create_shouldSaveAndReturnResponse() {
        // given
        var req = new ProductRequest("iPhone", "desc",
                new BigDecimal("99.99"), 5);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            return p;
        });

        // when
        ProductResponse res = productService.create(req);

        // then
        assertEquals(1L, res.id());
        assertEquals("iPhone", res.name());
        assertEquals("desc", res.description());
        assertEquals(new BigDecimal("99.99"), res.price());
        assertEquals(5, res.quantity());
        assertTrue(res.available());

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertFalse(captor.getValue().isDeleted());
    }

    @Test
    void update_shouldUpdateFields() {
        // given
        Product existing = Product.builder()
                .id(10L)
                .name("Old")
                .description("Old desc")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .deleted(false)
                .build();

        when(productRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var req = new ProductRequest("New", "New desc",
                new BigDecimal("20.00"), 3);

        // when
        ProductResponse res = productService.update(10L, req);

        // then
        assertEquals(10L, res.id());
        assertEquals("New", res.name());
        assertEquals("New desc", res.description());
        assertEquals(new BigDecimal("20.00"), res.price());
        assertEquals(3, res.quantity());
        assertTrue(res.available());

        verify(productRepository).findByIdAndDeletedFalse(10L);
        verify(productRepository).save(existing);
    }

    @Test
    void update_notFound_shouldThrow() {
        when(productRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        var req = new ProductRequest("X", null, new BigDecimal("1.00"), 0);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.update(99L, req)
        );

        assertTrue(ex.getMessage().contains("Product not found"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void softDelete_shouldMarkDeletedAndSave() {
        Product existing = Product.builder()
                .id(7L)
                .name("P")
                .price(new BigDecimal("5.00"))
                .quantity(1)
                .deleted(false)
                .build();

        when(productRepository.findByIdAndDeletedFalse(7L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.softDelete(7L);

        assertTrue(existing.isDeleted());
        assertNotNull(existing.getDeletedAt());

        verify(productRepository).save(existing);
    }

    @Test
    void getById_shouldReturnResponse() {
        Product p = Product.builder()
                .id(1L)
                .name("P")
                .description("D")
                .price(new BigDecimal("9.00"))
                .quantity(0)
                .deleted(false)
                .build();

        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(p));

        ProductResponse res = productService.getById(1L);

        assertEquals(1L, res.id());
        assertFalse(res.available()); // quantity = 0 => not available
    }

    @Test
    void search_minPriceGreaterThanMaxPrice_shouldThrow() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(IllegalArgumentException.class,
                () -> productService.search(null, new BigDecimal("100"), new BigDecimal("10"), null, pageable)
        );

        verify(productRepository, never())
                .findAll(ArgumentMatchers.<Specification<Product>>any(), any(Pageable.class));
    }

    @Test
    void search_shouldCallRepositoryAndMapToResponse() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("price").descending());

        Product p1 = Product.builder()
                .id(1L).name("A").price(new BigDecimal("10.00")).quantity(2).deleted(false)
                .build();
        Product p2 = Product.builder()
                .id(2L).name("B").price(new BigDecimal("20.00")).quantity(0).deleted(false)
                .build();

        Page<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);

        // IMPORTANT: this requires ProductRepository extends JpaSpecificationExecutor<Product>
        when(productRepository.findAll(Mockito.<Specification<Product>>any(), eq(pageable)))
                .thenReturn(page);

        Page<ProductResponse> res = productService.search("x", null, null, null, pageable);

        assertEquals(2, res.getTotalElements());
        assertEquals(2, res.getContent().size());
        assertTrue(res.getContent().get(0).available());   // qty 2
        assertFalse(res.getContent().get(1).available());  // qty 0

        verify(productRepository)
                .findAll(Mockito.<Specification<Product>>any(), eq(pageable));
    }

}