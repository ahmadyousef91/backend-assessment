package com.ahmedyousef.backend_assessment.application.product;

import com.ahmedyousef.backend_assessment.api.dto.CachedPage;
import com.ahmedyousef.backend_assessment.api.dto.ProductResponse;
import com.ahmedyousef.backend_assessment.api.mapper.ProductMapper;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.ahmedyousef.backend_assessment.infrastructure.cache.RedisCacheConfig.PRODUCT_LIST_CACHE;

@Service
@RequiredArgsConstructor

public class ProductSearchCacheService {

    private final ProductRepository productRepository;

    @Cacheable(
            cacheNames = PRODUCT_LIST_CACHE,
            key = "'p=' + #pageable.pageNumber + ':s=' + #pageable.pageSize " +
                    "+ ':name=' + (#name == null ? '' : #name) " +
                    "+  ':min=' + (#minPrice == null ? '' : #minPrice) + ':max=' " +
                    "+ (#maxPrice == null ? '' : #maxPrice) " +
                    "+ ':avail=' + (#available == null ? '' : #available)" +
                    " + ':sort=' + #pageable.sort.toString().replaceAll(' ', '')",
            unless = "#result == null || #result.totalElements() == 0"
    )
    @Transactional
    public CachedPage<ProductResponse> searchCached(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean available,
            Pageable pageable
    ) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must be <= maxPrice");
        }

        Specification<Product> spec = Specification.allOf(
                ProductSpecs.notDeleted(),
                ProductSpecs.nameContains(name),
                ProductSpecs.minPrice(minPrice),
                ProductSpecs.maxPrice(maxPrice),
                ProductSpecs.availability(available)
        );

        var page = productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);

        return new CachedPage<>(
                new java.util.ArrayList<>(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                pageable.getSort().toString()
        );
    }

}
