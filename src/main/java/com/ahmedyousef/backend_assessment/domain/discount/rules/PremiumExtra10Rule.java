package com.ahmedyousef.backend_assessment.domain.discount.rules;

import com.ahmedyousef.backend_assessment.domain.discount.DiscountContext;
import com.ahmedyousef.backend_assessment.domain.discount.DiscountRule;
import com.ahmedyousef.backend_assessment.domain.enums.UserRole;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PremiumExtra10Rule implements DiscountRule {

    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");


    @Override
    public boolean matches(DiscountContext discountContext) {
        return discountContext.role() == UserRole.PREMIUM_USER;
    }

    @Override
    public BigDecimal discountAmount(DiscountContext discountContext) {
        return discountContext.subtotal()
                .multiply(DISCOUNT_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
