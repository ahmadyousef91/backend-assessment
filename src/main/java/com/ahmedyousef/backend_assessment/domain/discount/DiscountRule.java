package com.ahmedyousef.backend_assessment.domain.discount;

import java.math.BigDecimal;

public interface DiscountRule {

    boolean matches(DiscountContext discountContext);
    BigDecimal discountAmount(DiscountContext discountContext);
}
