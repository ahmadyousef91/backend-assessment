package com.ahmedyousef.backend_assessment.domain.discount.allocation;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.ahmedyousef.backend_assessment.domain.discount.Money.fromCents;
import static com.ahmedyousef.backend_assessment.domain.discount.Money.toCents;

public final class ProportionalDiscountAllocator {

    private ProportionalDiscountAllocator() {
    }

    public static Map<Long, BigDecimal> allocate(BigDecimal orderDiscount, List<OrderLine> lines) {
        Map<Long, BigDecimal> out = new LinkedHashMap<>();
        if (lines == null || lines.isEmpty()) return out;

        long discountCents = toCents(orderDiscount);
        if (discountCents <= 0) {
            for (OrderLine l : lines) out.put(l.key(), fromCents(0));
            return out;
        }

        long subtotalCents = 0;
        long[] lineCents = new long[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            long c = toCents(lines.get(i).lineSubtotal());
            if (c < 0) throw new IllegalArgumentException("Line subtotal cannot be negative");
            lineCents[i] = c;
            subtotalCents += c;
        }

        if (subtotalCents == 0) {
            for (OrderLine l : lines) out.put(l.key(), fromCents(0));
            return out;
        }

        if (discountCents > subtotalCents) discountCents = subtotalCents;

        long allocated = 0;
        for (int i = 0; i < lines.size(); i++) {
            long itemDiscountCents;

            if (i == lines.size() - 1) {
                itemDiscountCents = discountCents - allocated;
            } else {
                itemDiscountCents = (discountCents * lineCents[i]) / subtotalCents; // floor
                allocated += itemDiscountCents;
            }

            if (itemDiscountCents > lineCents[i]) itemDiscountCents = lineCents[i];
            out.put(lines.get(i).key(), fromCents(itemDiscountCents));
        }
        return out;
    }
}
