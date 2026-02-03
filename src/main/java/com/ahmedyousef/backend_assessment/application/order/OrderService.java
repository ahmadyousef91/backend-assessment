package com.ahmedyousef.backend_assessment.application.order;

import com.ahmedyousef.backend_assessment.api.dto.PlaceOrderRequest;
import com.ahmedyousef.backend_assessment.api.dto.PlaceOrderResponse;
import com.ahmedyousef.backend_assessment.application.order.exception.InsufficientStockException;
import com.ahmedyousef.backend_assessment.application.order.exception.NotFoundException;
import com.ahmedyousef.backend_assessment.domain.discount.DiscountCalculator;
import com.ahmedyousef.backend_assessment.domain.discount.DiscountContext;
import com.ahmedyousef.backend_assessment.domain.discount.allocation.OrderLine;
import com.ahmedyousef.backend_assessment.domain.discount.allocation.ProportionalDiscountAllocator;
import com.ahmedyousef.backend_assessment.domain.entity.Order;
import com.ahmedyousef.backend_assessment.domain.entity.OrderItem;
import com.ahmedyousef.backend_assessment.domain.entity.Product;
import com.ahmedyousef.backend_assessment.domain.entity.User;
import com.ahmedyousef.backend_assessment.domain.enums.OrderStatus;
import com.ahmedyousef.backend_assessment.domain.repository.OrderRepository;
import com.ahmedyousef.backend_assessment.domain.repository.ProductRepository;
import com.ahmedyousef.backend_assessment.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ahmedyousef.backend_assessment.domain.discount.Money.money;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final DiscountCalculator discountCalculator;
    private final OrderRepository orderRepo;


    @Transactional
    public PlaceOrderResponse placeOrder(PlaceOrderRequest req) {

        User user = userRepo.findById(req.userId()).orElseThrow(() ->
                new NotFoundException("User not found " + req.userId()));

        if (req.items() == null || req.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain items");
        }

        List<Long> orderProductIds = req.items().stream()
                .map(PlaceOrderRequest.Item::productId)
                .toList();
        List<Product> products = productRepo.findAllByIdInForUpdate(orderProductIds);

        Map<Long, Product> productById = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (PlaceOrderRequest.Item item : req.items()) {
            Product product = productById.get(item.productId());
            if (product == null) {
                throw new NotFoundException("Product not found: " + item.productId());
            }
            if (item.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be > 0");
            }

            if (product.getQuantity() < item.quantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product " + product.getId());
            }
        }

        Order customerOrder = Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .build();

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;
        for (PlaceOrderRequest.Item item : req.items()) {
            Product product = productById.get(item.productId());
            BigDecimal unitPrice = money(product.getPrice());
            BigDecimal lineSubtotal = money(unitPrice.multiply(BigDecimal.valueOf(item.quantity())));
            OrderItem orderItem = OrderItem.builder()
                    .order(customerOrder)
                    .product(product)
                    .quantity(item.quantity())
                    .unitPrice(unitPrice)
                    .discountApplied(money(BigDecimal.ZERO))
                    .totalPrice(lineSubtotal)
                    .build();
            items.add(orderItem);
            subTotal = subTotal.add(lineSubtotal);
        }
        subTotal = money(subTotal);
        BigDecimal discountTotal = discountCalculator
                .computeTotalDiscount(new DiscountContext(user.getRole(), subTotal));
        discountTotal = money(discountTotal);

        List<OrderLine> lines = items.stream()
                .map(oi -> new OrderLine(
                        oi.getProduct().getId(),
                        money(oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                ))
                .toList();

        Map<Long, BigDecimal> allocated = ProportionalDiscountAllocator.allocate(discountTotal, lines);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem oi : items) {
            BigDecimal lineSubtotal = money(oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
            BigDecimal itemDiscount = money(allocated.getOrDefault(oi.getProduct().getId(), BigDecimal.ZERO));

            BigDecimal itemTotal = money(lineSubtotal.subtract(itemDiscount));

            oi.setDiscountApplied(itemDiscount);
            oi.setTotalPrice(itemTotal);

            total = total.add(itemTotal);
        }
        total = money(total);

        // 6) Set order totals + attach items
        customerOrder.setSubtotal(subTotal);
        customerOrder.setDiscountTotal(discountTotal);
        customerOrder.setTotal(total);
        customerOrder.getItems().addAll(items);

        // 7) Decrease inventory (still inside same transaction + locks held)
        for (PlaceOrderRequest.Item it : req.items()) {
            Product p = productById.get(it.productId());
            p.setQuantity(p.getQuantity() - it.quantity());
        }

        // 8) Persist (cascade saves items)
        Order savedorder = orderRepo.save(customerOrder);

        // 9) Build response
        List<PlaceOrderResponse.Item> respItems = items.stream()
                .map(oi -> new PlaceOrderResponse.Item(
                        oi.getProduct().getId(),
                        oi.getQuantity(),
                        oi.getUnitPrice(),
                        oi.getDiscountApplied(),
                        oi.getTotalPrice()
                ))
                .toList();

        return new PlaceOrderResponse(savedorder.getId(), subTotal, discountTotal, total, respItems);

    }
}
