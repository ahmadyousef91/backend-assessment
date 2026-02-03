package com.ahmedyousef.backend_assessment.domain.discount;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.ahmedyousef.backend_assessment.domain.discount.Money.money;
@Component
public class DiscountCalculator {

    private final List<DiscountRule> rules;

    public DiscountCalculator(List<DiscountRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public BigDecimal computeTotalDiscount(DiscountContext ctx) {
        BigDecimal subtotal = money(ctx.subtotal());
        if (subtotal.signum() < 0) throw new IllegalArgumentException("Subtotal cannot be negative");

        BigDecimal discount = rules.stream()
                .filter(r -> r.matches(ctx))
                .map(r -> money(r.discountAmount(ctx)))
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add);

        discount = money(discount);

        if (discount.compareTo(subtotal) > 0) discount = subtotal;
        return discount;
    }
}
