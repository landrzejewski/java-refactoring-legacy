package pl.training.module4.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record PriceBreakdown(
        BigDecimal baseRentalCost,
        BigDecimal discount,
        BigDecimal insuranceCost,
        BigDecimal deliveryCost,
        BigDecimal netAmount,
        BigDecimal vat,
        BigDecimal total) {
    public PriceBreakdown {
        baseRentalCost = money(baseRentalCost, "baseRentalCost");
        discount = money(discount, "discount");
        insuranceCost = money(insuranceCost, "insuranceCost");
        deliveryCost = money(deliveryCost, "deliveryCost");
        netAmount = money(netAmount, "netAmount");
        vat = money(vat, "vat");
        total = money(total, "total");
    }

    private static BigDecimal money(BigDecimal amount, String name) {
        Objects.requireNonNull(amount, name);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }
}
