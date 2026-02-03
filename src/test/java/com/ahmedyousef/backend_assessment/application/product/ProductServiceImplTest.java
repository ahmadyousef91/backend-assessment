package com.ahmedyousef.backend_assessment.application.product;

import com.ahmedyousef.backend_assessment.api.dto.CachedPage;
import com.ahmedyousef.backend_assessment.api.dto.ProductRequest;
import com.ahmedyousef.backend_assessment.api.dto.ProductResponse;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    ProductSearchCacheService searchCacheService;

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
    void search_shouldCallCacheService_andReturnPage() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        var cached = new CachedPage<ProductResponse>(
                List.of(new ProductResponse(1L, "P1", "D", new BigDecimal("10.00"), 5, true, null, null)), // adapt to your DTO
                0, 20, 1L,
                "createdAt:DESC"
        );

        when(searchCacheService.searchCached(
                eq("P1"),
                eq(new BigDecimal("1.00")),
                eq(new BigDecimal("20.00")),
                eq(true),
                eq(pageable)
        )).thenReturn(cached);

        Page<ProductResponse> result =
                productService.search("P1", new BigDecimal("1.00"), new BigDecimal("20.00"), true, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("P1", result.getContent().get(0).name()); // adapt getter/field

        verify(searchCacheService).searchCached("P1", new BigDecimal("1.00"), new BigDecimal("20.00"), true, pageable);
        verifyNoInteractions(productRepository); // optional: because caching service owns repository call now
    }

}