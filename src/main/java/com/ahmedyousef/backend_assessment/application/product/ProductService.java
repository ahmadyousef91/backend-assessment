package com.ahmedyousef.backend_assessment.application.product;

import com.ahmedyousef.backend_assessment.api.dto.ProductRequest;
import com.ahmedyousef.backend_assessment.api.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponse create(ProductRequest req);
    ProductResponse update(Long id, ProductRequest req);
    void softDelete(Long id);
    ProductResponse getById(Long id);
    Page<ProductResponse> search(String name, BigDecimal minPrice, BigDecimal maxPrice,
                                 Boolean available, Pageable pageable);
}
