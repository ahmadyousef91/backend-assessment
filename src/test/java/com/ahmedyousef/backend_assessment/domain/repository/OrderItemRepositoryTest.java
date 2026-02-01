package com.ahmedyousef.backend_assessment.domain.repository;

import com.ahmedyousef.backend_assessment.domain.entity.Order;
import com.ahmedyousef.backend_assessment.domain.entity.OrderItem;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.entity.User;
import com.ahmedyousef.backend_assessment.domain.enums.OrderStatus;
import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    @Test
    void findByOrderId_and_findByProductId_shouldWork() {
        User user = userRepository.save(User.builder()
                .username("buyer5")
                .role(UserRole.values()[0])
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Headset")
                .description("Wireless")
                .price(new BigDecimal("200.00"))
                .quantity(100)
                .build());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.values()[0])
                .subtotal(new BigDecimal("400.00"))
                .discountTotal(new BigDecimal("0.00"))
                .total(new BigDecimal("400.00"))
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("200.00"))
                .totalPrice(new BigDecimal("400.00"))
                .build();

        order.addItem(item);
        Order savedOrder = orderRepository.save(order);

        assertThat(orderItemRepository.findByOrderId(savedOrder.getId())).hasSize(1);
        assertThat(orderItemRepository.findByProductId(product.getId())).hasSize(1);
    }
}
