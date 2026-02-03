package com.ahmedyousef.backend_assessment.application.product;

import com.ahmedyousef.backend_assessment.api.dto.CachedPage;
import com.ahmedyousef.backend_assessment.api.dto.ProductRequest;
import com.ahmedyousef.backend_assessment.api.dto.ProductResponse;
import com.ahmedyousef.backend_assessment.api.mapper.ProductMapper;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.ahmedyousef.backend_assessment.infrastructure.cache.RedisCacheConfig.PRODUCT_LIST_CACHE;


@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchCacheService searchCacheService;

    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
    @Override
    public ProductResponse create(ProductRequest req) {
        Product product = Product.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .quantity(req.quantity())
                .deleted(false)
                .build();
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
    @Override
    public ProductResponse update(Long id, ProductRequest req) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setQuantity(req.quantity());

        return ProductMapper.toResponse(productRepository.save(product));
    }

    @CacheEvict(cacheNames = PRODUCT_LIST_CACHE, allEntries = true)
    @Override
    public void softDelete(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        product.softDelete();
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductResponse getById(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        return ProductMapper.toResponse(product);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<ProductResponse> search(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean available,
            Pageable pageable
    ) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must be <= maxPrice");
        }

        CachedPage<ProductResponse> cached =
                searchCacheService.searchCached(name, minPrice, maxPrice, available, pageable);

        Pageable effective = PageRequest.of(cached.pageNumber(), cached.pageSize(), pageable.getSort());
        return new PageImpl<>(cached.content(), effective, cached.totalElements());
    }

}
