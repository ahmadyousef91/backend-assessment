package com.ahmedyousef.backend_assessment.application.product;

import com.ahmedyousef.backend_assessment.api.dto.ProductRequest;
import com.ahmedyousef.backend_assessment.api.dto.ProductResponse;
import com.ahmedyousef.backend_assessment.api.mapper.ProductMapper;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

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
    @Transactional
    public Page<ProductResponse> search(String name, BigDecimal minPrice,
                                        BigDecimal maxPrice, Boolean available,
                                        Pageable pageable) {
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


        return productRepository.findAll(spec, pageable).map(ProductMapper::toResponse);
    }
}
