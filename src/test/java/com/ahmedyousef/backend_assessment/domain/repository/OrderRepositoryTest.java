package com.ahmedyousef.backend_assessment.domain.repository;

import com.ahmedyousef.backend_assessment.domain.entity.Order;
import com.ahmedyousef.backend_assessment.domain.entity.OrderItem;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.entity.User;
import com.ahmedyousef.backend_assessment.domain.enums.OrderStatus;
import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void saveOrder_shouldCascadeSaveItems() {
        User user = userRepository.save(User.builder()
                .username("buyer1")
                .role(UserRole.values()[0])
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Keyboard")
                .description("Mechanical")
                .price(new BigDecimal("300.00"))
                .quantity(20)
                .build());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.values()[0])
                .subtotal(new BigDecimal("600.00"))
                .discountTotal(new BigDecimal("0.00"))
                .total(new BigDecimal("600.00"))
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("300.00"))
                .discountApplied(new BigDecimal("0.00"))
                .totalPrice(new BigDecimal("600.00"))
                .build();

        order.addItem(item);

        Order saved = orderRepository.save(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getId()).isNotNull();
    }

    @Test
    void findByUserId_shouldReturnOrdersForThatUser() {
        User user1 = userRepository.save(User.builder()
                .username("buyer2")
                .role(UserRole.values()[0])
                .build());

        User user2 = userRepository.save(User.builder()
                .username("buyer3")
                .role(UserRole.values()[0])
                .build());

        orderRepository.save(Order.builder()
                .user(user1)
                .status(OrderStatus.values()[0])
                .subtotal(new BigDecimal("0.00"))
                .discountTotal(new BigDecimal("0.00"))
                .total(new BigDecimal("0.00"))
                .build());

        orderRepository.save(Order.builder()
                .user(user2)
                .status(OrderStatus.values()[0])
                .subtotal(new BigDecimal("0.00"))
                .discountTotal(new BigDecimal("0.00"))
                .total(new BigDecimal("0.00"))
                .build());

        assertThat(orderRepository.findByUserId(user1.getId())).hasSize(1);
        assertThat(orderRepository.findByUserId(user2.getId())).hasSize(1);
    }

    @Test
    void loadOrder_thenAccessItems_shouldWorkInsideTransaction() {
        User user = userRepository.save(User.builder()
                .username("buyer4")
                .role(UserRole.values()[0])
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Monitor")
                .description("27 inch")
                .price(new BigDecimal("900.00"))
                .quantity(5)
                .build());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.values()[0])
                .subtotal(new BigDecimal("900.00"))
                .discountTotal(new BigDecimal("0.00"))
                .total(new BigDecimal("900.00"))
                .build();

        order.addItem(OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(new BigDecimal("900.00"))
                .totalPrice(new BigDecimal("900.00"))
                .build());

        Order saved = orderRepository.save(order);

        entityManager.flush();
        entityManager.clear();

        Order loaded = orderRepository.findById(saved.getId()).orElseThrow();

        assertThat(loaded.getItems()).hasSize(1);
        assertThat(loaded.getItems().get(0).getProduct().getName()).isEqualTo("Monitor");
    }
}
