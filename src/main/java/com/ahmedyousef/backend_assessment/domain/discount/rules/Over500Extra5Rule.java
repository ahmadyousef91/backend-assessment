package com.ahmedyousef.backend_assessment.domain.discount.rules;

import com.ahmedyousef.backend_assessment.domain.discount.DiscountContext;
import com.ahmedyousef.backend_assessment.domain.discount.DiscountRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class Over500Extra5Rule implements DiscountRule {

    private static final BigDecimal ELIGIBILITY_AMOUNT = new BigDecimal("500.00");
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.05");


    @Override
    public boolean matches(DiscountContext discountContext) {
        return discountContext.subtotal().compareTo(ELIGIBILITY_AMOUNT) > 0;
    }

    @Override
    public BigDecimal discountAmount(DiscountContext discountContext) {
        return discountContext.subtotal()
                .multiply(DISCOUNT_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
